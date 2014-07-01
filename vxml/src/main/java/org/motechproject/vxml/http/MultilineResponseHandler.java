package org.motechproject.vxml.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.alert.MotechStatusMessage;
import org.motechproject.vxml.audit.CallRecord;
import org.motechproject.vxml.audit.CallStatus;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.templates.Template;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.vxml.VxmlEvents.outboundEvent;
import static org.motechproject.vxml.audit.CallDirection.OUTBOUND;

/**
 * Deals with multi-line responses, like the ones sent by Clickatell
 */
public class MultilineResponseHandler extends ResponseHandler {

    MultilineResponseHandler(Template template, Config config, MotechStatusMessage motechStatusMessage) {
        super(template, config, motechStatusMessage);
    }

    @Override
    public void handle(OutgoingVxml vxml, String response, Header[] headers) {

        //
        // as the class name suggest we're dealing with a provider which returns a status code for each individual
        // recipient phone number in the original request on a separate line, ie: if we send an VXML to 4 recipients
        // then we should receive four lines of provider_message_id & status information
        //
        for (String responseLine : response.split("\\r?\\n")) {

            String[] messageIdAndRecipient = getTemplateOutgoingResponse().extractSuccessMessageIdAndRecipient(
                    responseLine);

            if (messageIdAndRecipient == null) {
                Integer failureCount = vxml.getFailureCount() + 1;
                String[] messageAndRecipient;

                messageAndRecipient = getTemplateOutgoingResponse().extractFailureMessageAndRecipient(responseLine);
                if (messageAndRecipient == null) {
                    getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                            vxml.getRecipients(), vxml.getMessage(), vxml.getMotechId(), null, failureCount, null, null));

                    String errorMessage = String.format(
                            "Failed to send VXML. Template error. Can't parse response: %s", responseLine);
                    getLogger().error(errorMessage);
                    getMotechStatusMessage().alert(errorMessage);

                    getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, vxml.getRecipients().toString(),
                            vxml.getMessage(), now(), getConfig().retryOrAbortStatus(failureCount), null,
                            vxml.getMotechId(), null, null));
                } else {
                    String failureMessage = messageAndRecipient[0];
                    String recipient = messageAndRecipient[1];
                    List<String> recipients = Arrays.asList(new String[]{recipient});
                    getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                            recipients, vxml.getMessage(), vxml.getMotechId(), null, failureCount, null, null));
                    getLogger().info("Failed to send VXML: {}", failureMessage);
                    getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, recipient, vxml.getMessage(),
                            now(), getConfig().retryOrAbortStatus(failureCount), null, vxml.getMotechId(), null,
                            failureMessage));
                }
            } else {
                String messageId = messageIdAndRecipient[0];
                String recipient = messageIdAndRecipient[1];
                List<String> recipients = Arrays.asList(new String[]{recipient});
                //todo: HIPAA concerns?
                getLogger().info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(vxml),
                        recipient));
                getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, recipient, vxml.getMessage(), now(),
                        CallStatus.DISPATCHED, null, vxml.getMotechId(), messageId, null));
                getEvents().add(outboundEvent(CallEventSubjects.DISPATCHED, getConfig().getName(), recipients,
                        vxml.getMessage(), vxml.getMotechId(), messageId, null, null, null));
            }
        }
    }
}
