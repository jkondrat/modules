package org.motechproject.vxml.service;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.VxmlEventParams;
import org.motechproject.vxml.audit.CallRecord;
import org.motechproject.vxml.audit.CallStatus;
import org.motechproject.vxml.audit.AuditService;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.configs.ConfigReader;
import org.motechproject.vxml.configs.Configs;
import org.motechproject.vxml.templates.Template;
import org.motechproject.vxml.templates.TemplateReader;
import org.motechproject.vxml.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.vxml.VxmlEvents.outboundEvent;
import static org.motechproject.vxml.audit.CallDirection.OUTBOUND;

//todo: final pass over how we use motechId system-wide

/**
 * Send an VXML, we really don't send here, but rather pass it on to the VxmlHttpService which does
 */
@Service("vxmlService")
public class VxmlServiceImpl implements VxmlService {

    private SettingsFacade settingsFacade;
    private Logger logger = LoggerFactory.getLogger(VxmlServiceImpl.class);
    private EventRelay eventRelay;
    private MotechSchedulerService schedulerService;
    private Templates templates;
    private AuditService auditService;

    @Autowired
    public VxmlServiceImpl(@Qualifier("vxmlSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                          MotechSchedulerService schedulerService, TemplateReader templateReader,
                          AuditService auditService) {
        //todo: persist configs or reload them for each call?
        //todo: right now I'm doing the latter...
        //todo: ... but I'm not wed to it.
        this.settingsFacade = settingsFacade;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
        templates = templateReader.getTemplates();
        this.auditService = auditService;
    }

    private static List<String> splitMessage(String message, int maxSize, String header, String footer,
                                             boolean excludeLastFooter) {
        List<String> parts = new ArrayList<String>();
        int messageLength = message.length();

        if (messageLength <= maxSize) {
            parts.add(message);
        } else {
            //NOTE: since the format placeholders $m and $t are two characters wide and we assume no more than
            //99 parts, we don't need to do a String.format() to figure out the length of the actual header/footer
            String headerTemplate = header + "\n";
            String footerTemplate = "\n" + footer;
            int textSize = maxSize - headerTemplate.length() - footerTemplate.length();
            Integer numberOfParts = (int) Math.ceil(messageLength / (double) textSize);
            String numberOfPartsString = numberOfParts.toString();

            for (Integer i = 1; i <= numberOfParts; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(headerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                if (i.equals(numberOfParts)) {
                    sb.append(message.substring((i - 1) * textSize));
                    if (!excludeLastFooter) {
                        sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                    }
                } else {
                    sb.append(message.substring((i - 1) * textSize, (i - 1) * textSize + textSize));
                    sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                }
                parts.add(sb.toString());
            }
        }
        return parts;
    }

    private List<List<String>> splitRecipientList(List<String> list, Integer maxSize) {
        List<List<String>> ret = new ArrayList<>();
        int i = 0;
        ArrayList<String> chunk = new ArrayList<String>();
        for (String val : list) {
            chunk.add(val);
            i++;
            if (i % maxSize == 0) {
                ret.add(chunk);
                chunk = new ArrayList<String>();
            }
        }
        if (chunk.size() > 0) {
            ret.add(chunk);
        }
        return ret;
    }

    private String generateMotechId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    /**
     * Sends an VXML
     */
    public void send(OutgoingVxml vxml) {

        //todo: cache that?
        Configs configs = new ConfigReader(settingsFacade).getConfigs();
        Config config;
        Template template;

        if (vxml.hasConfig()) {
            config = configs.getConfig(vxml.getConfig());
        } else {
            logger.debug("No config specified, using default config.");
            config = configs.getDefaultConfig();
        }
        template = templates.getTemplate(config.getTemplateName());

        //todo: die if things aren't right, right?
        //todo: VXML_SCHEDULE_FUTURE_VXML research if any vxml provider provides that, for now assume not.

        Integer maxSize = template.getOutgoing().getMaxVxmlSize();
        String header = config.getSplitHeader();
        String footer = config.getSplitFooter();
        Boolean excludeLastFooter = config.getExcludeLastFooter();
        //todo: maximum number of supported recipients : per template/provider and/or per http specs

        //todo - cr - move that to the Config object so calculated only once ?
        //todo - cr - investigate if that might be a problem on windows
        // -2 to account for the added \n after the header and before the footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException(
                    "The combined sizes of the header and footer templates are larger than the maximum VXML size!");
        }

        List<String> messageParts = splitMessage(vxml.getMessage(), maxSize, header, footer, excludeLastFooter);
        List<List<String>> recipientsList = splitRecipientList(vxml.getRecipients(),
                template.getOutgoing().getMaxRecipient());

        //todo: delivery_time on the vxml provider's side if they support it?
        for (List<String> recipients : recipientsList) {
            if (vxml.hasDeliveryTime()) {
                DateTime dt = vxml.getDeliveryTime();
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    MotechEvent event = outboundEvent(CallEventSubjects.SCHEDULED, config.getName(), recipients, part,
                            motechId, null, null, null, null);
                    //MOTECH scheduler needs unique job ids, so adding motechId as job_id_key will do that
                    event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, motechId);
                    event.getParameters().put(VxmlEventParams.DELIVERY_TIME, dt);
                    schedulerService.safeScheduleRunOnceJob(new RunOnceSchedulableJob(event, dt.toDate()));
                    logger.info(String.format("Scheduling message [%s] to [%s] at %s.",
                            part.replace("\n", "\\n"), recipients, vxml.getDeliveryTime()));
                    //add one millisecond to the next vxml part so they will be delivered in order
                    //without that it seems Quartz doesn't fire events in the order they were scheduled
                    dt = dt.plus(1);
                    for (String recipient : recipients) {
                        auditService.log(new CallRecord(config.getName(), OUTBOUND, recipient, part, now(),
                                CallStatus.SCHEDULED, null, motechId, null, null));
                    }
                }
            } else {
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    eventRelay.sendEventMessage(outboundEvent(CallEventSubjects.PENDING, config.getName(), recipients,
                            part, motechId, null, null, null, null));
                    logger.info("Sending message [{}] to [{}].", part.replace("\n", "\\n"), recipients);
                    for (String recipient : recipients) {
                        auditService.log(new CallRecord(config.getName(), OUTBOUND, recipient, part, now(),
                                CallStatus.PENDING, null, motechId, null, null));
                    }
                }
            }
        }
    }
}
