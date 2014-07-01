package org.motechproject.vxml.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.alert.MotechStatusMessage;
import org.motechproject.vxml.audit.CallRecord;
import org.motechproject.vxml.audit.CallStatus;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.templates.Template;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.vxml.VxmlEvents.outboundEvent;
import static org.motechproject.vxml.audit.CallDirection.OUTBOUND;

/**
 * Deals with providers who return multi-line responses, but return a different response when sending only one message,
 * like Clickatell does
 */
public class MultilineSingleResponseHandler extends ResponseHandler {

    MultilineSingleResponseHandler(Template template, Config config, MotechStatusMessage motechStatusMessage) {
        super(template, config, motechStatusMessage);
    }

    @Override
    public void handle(OutgoingVxml vxml, String response, Header[] headers) {

        String messageId = getTemplateOutgoingResponse().extractSingleSuccessMessageId(response);

        if (messageId == null) {
            Integer failureCount = vxml.getFailureCount() + 1;

            String failureMessage = getTemplateOutgoingResponse().extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                    vxml.getRecipients(), vxml.getMessage(), vxml.getMotechId(), null, failureCount, null, null));
            getLogger().info("Failed to send VXML: {}", failureMessage);
            getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, vxml.getRecipients().get(0),
                    vxml.getMessage(), now(), getConfig().retryOrAbortStatus(failureCount), null, vxml.getMotechId(),
                    null, failureMessage));
        } else {
            //todo: HIPAA concerns?
            getLogger().info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(vxml),
                    vxml.getRecipients().get(0)));
            getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, vxml.getRecipients().get(0),
                    vxml.getMessage(), now(), CallStatus.DISPATCHED, null, vxml.getMotechId(), messageId, null));
            getEvents().add(outboundEvent(CallEventSubjects.DISPATCHED, getConfig().getName(), vxml.getRecipients(),
                    vxml.getMessage(), vxml.getMotechId(), messageId, null, null, null));
        }
    }
}
