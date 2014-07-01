package org.motechproject.vxml.web;

import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.alert.MotechStatusMessage;
import org.motechproject.vxml.audit.AuditService;
import org.motechproject.vxml.audit.CallRecord;
import org.motechproject.vxml.audit.CallRecords;
import org.motechproject.vxml.audit.CallStatus;
import org.motechproject.vxml.audit.VxmlRecordSearchCriteria;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.configs.ConfigReader;
import org.motechproject.vxml.configs.Configs;
import org.motechproject.vxml.templates.Status;
import org.motechproject.vxml.templates.Template;
import org.motechproject.vxml.templates.TemplateReader;
import org.motechproject.vxml.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.vxml.VxmlEvents.outboundEvent;
import static org.motechproject.vxml.audit.CallDirection.OUTBOUND;

/**
 * Handles message delivery status updates sent by vxml providers to
 * {motechserver}/motech-platform-server/module/vxml/status{Config}
 */
@Controller
@RequestMapping(value = "/status")
public class StatusController {

    @Autowired
    private MotechStatusMessage motechStatusMessage;
    private Logger logger = LoggerFactory.getLogger(StatusController.class);
    private ConfigReader configReader;
    private Configs configs;
    private Templates templates;
    private EventRelay eventRelay;
    private AuditService auditService;
    private static final int RECORD_FIND_RETRY_COUNT = 3;
    private static final int RECORD_FIND_TIMEOUT = 500;

    @Autowired
    public StatusController(@Qualifier("vxmlSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                            TemplateReader templateReader, AuditService auditService) {
        this.eventRelay = eventRelay;
        configReader = new ConfigReader(settingsFacade);
        //todo: this means we'd crash/error out when a new config is created and we get a status update callback before
        //todo: restarting the module  -  so for now we'll read configs each time handle() gets called
        //todo: but ultimately we'll want something like: configs = configReader.getConfigs()
        templates = templateReader.getTemplates();
        this.auditService = auditService;
    }

    private CallRecord findFirstByProviderMessageId(CallRecords callRecords, String providerMessageId) {
        for (CallRecord callRecord : callRecords.getRecords()) {
            if (callRecord.getProviderId().equals(providerMessageId)) {
                return callRecord;
            }
        }
        return null;
    }

    private CallRecord findOrCreateVxmlRecord(String configName, String providerMessageId, String statusString) {
        int retry = 0;
        CallRecord callRecord;
        CallRecords callRecords;
        CallRecord existingCallRecord = null;
        QueryParams queryParams = new QueryParams(new Order("timestamp", Order.Direction.DESC));

        // Try to find an existing VXML record using the provider message ID
        // NOTE: Only works if the provider guarantees the message id is unique. So far, all do.
        do {
            //seems that lucene takes a while to index, so try a couple of times and delay in between
            if (retry > 0) {
                logger.debug("Trying again to find log record with motechId {}, try {}", providerMessageId, retry + 1);
                try {
                    Thread.sleep(RECORD_FIND_TIMEOUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            callRecords = auditService.findAllVxmlRecords(new VxmlRecordSearchCriteria()
                    .withConfig(configName)
                    .withProviderId(providerMessageId)
                    .withQueryParams(queryParams));
            retry++;
        } while (retry < RECORD_FIND_RETRY_COUNT && CollectionUtils.isEmpty(callRecords.getRecords()));

        if (CollectionUtils.isEmpty(callRecords.getRecords())) {
            // If we couldn't find a record by provider message ID try using the MOTECH ID
            callRecords = auditService.findAllVxmlRecords(new VxmlRecordSearchCriteria()
                    .withConfig(configName)
                    .withMotechId(providerMessageId)
                    .withQueryParams(queryParams));
            if (!CollectionUtils.isEmpty(callRecords.getRecords())) {
                logger.debug("Found log record with matching motechId {}", providerMessageId);
                existingCallRecord = callRecords.getRecords().get(0);
            }
        } else {
            //todo: temporary kludge: lucene can't find exact strings, so we're looping on results until we find
            //todo: an exact match. Remove when we switch to SEUSS.
            existingCallRecord = findFirstByProviderMessageId(callRecords, providerMessageId);
            if (existingCallRecord != null) {
                logger.debug("Found log record with matching providerId {}", providerMessageId);
            }
        }

        if (existingCallRecord == null) {
            String msg = String.format("Received status update but couldn't find a log record with matching " +
                    "ProviderMessageId or motechId: %s", providerMessageId);
            logger.error(msg);
            motechStatusMessage.alert(msg);
        }

        if (existingCallRecord != null) {
            callRecord = new CallRecord(configName, OUTBOUND, existingCallRecord.getPhoneNumber(),
                    existingCallRecord.getMessageContent(), now(), null, statusString,
                    existingCallRecord.getMotechId(), providerMessageId, null);
        } else {
            //start with an empty VXML record
            callRecord = new CallRecord(configName, OUTBOUND, null, null, now(), null, statusString, null,
                    providerMessageId, null);
        }

        return callRecord;
    }

    private void analyzeStatus(Status status, String configName, Map<String, String> params) {
        String statusString = params.get(status.getStatusKey());
        String providerMessageId = params.get(status.getMessageIdKey());
        CallRecord callRecord = findOrCreateVxmlRecord(configName, providerMessageId, statusString);
        List<String> recipients = Arrays.asList(callRecord.getPhoneNumber());

        if (statusString != null) {
            String eventSubject;
            if (statusString.matches(status.getStatusSuccess())) {
                callRecord.setCallStatus(CallStatus.DELIVERY_CONFIRMED);
                eventSubject = CallEventSubjects.DELIVERY_CONFIRMED;
            } else if (status.hasStatusFailure() && statusString.matches(status.getStatusFailure())) {
                callRecord.setCallStatus(CallStatus.FAILURE_CONFIRMED);
                eventSubject = CallEventSubjects.FAILURE_CONFIRMED;
            } else {
                // If we're not certain the message was delivered or failed, then it's in the DISPATCHED gray area
                callRecord.setCallStatus(CallStatus.DISPATCHED);
                eventSubject = CallEventSubjects.DISPATCHED;
            }
            eventRelay.sendEventMessage(outboundEvent(eventSubject, configName, recipients,
                    callRecord.getMessageContent(), callRecord.getMotechId(), providerMessageId, null, statusString,
                    now()));
        } else {
            String msg = String.format("Likely template error, unable to extract status string. Config: %s, Parameters: %s",
                    configName, params);
            logger.error(msg);
            motechStatusMessage.alert(msg);
            callRecord.setCallStatus(CallStatus.FAILURE_CONFIRMED);
            eventRelay.sendEventMessage(outboundEvent(CallEventSubjects.FAILURE_CONFIRMED, configName, recipients,
                    callRecord.getMessageContent(), callRecord.getMotechId(), providerMessageId, null, null,
                    now()));
        }
        auditService.log(callRecord);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{configName}")
    public void handle(@PathVariable String configName, @RequestParam Map<String, String> params) {
        logger.info("VXML Status - configName = {}, params = {}", configName, params);

        //todo: for now re-read configs every call, we'll change to the new config notifications when it's ready
        configs = configReader.getConfigs();

        Config config;
        if (configs.hasConfig(configName)) {
            config = configs.getConfig(configName);
        } else {
            String msg = String.format("Received VXML Status for '%s' config but no matching config: %s", configName,
                    params);
            logger.error(msg);
            motechStatusMessage.alert(msg);
            config = configs.getDefaultConfig();
        }
        Template template = templates.getTemplate(config.getTemplateName());
        Status status = template.getStatus();

        if (status.hasMessageIdKey() && params != null && params.containsKey(status.getMessageIdKey())) {
            if (status.hasStatusKey() && status.hasStatusSuccess()) {
                analyzeStatus(status, configName, params);
            } else {
                String msg = String.format("We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                        configName, params);
                logger.error(msg);
                motechStatusMessage.alert(msg);
            }
        } else {
            String msg = String.format("Status message received from provider, but no template support! Config: %s, Parameters: %s",
                    configName, params);
            logger.error(msg);
            motechStatusMessage.alert(msg);
        }
    }
}
