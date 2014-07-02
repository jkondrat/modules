package org.motechproject.vxml.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.alert.MotechStatusMessage;
import org.motechproject.vxml.log.CallRecord;
import org.motechproject.vxml.log.CallStatus;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.templates.Template;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.vxml.VxmlEvents.outboundEvent;
import static org.motechproject.vxml.log.CallDirection.OUTBOUND;

/**
 * Deals with providers who return a generic response in the body or header
 */
public class GenericResponseHandler extends ResponseHandler {

    GenericResponseHandler(Template template, Config config, MotechStatusMessage motechStatusMessage) {
        super(template, config, motechStatusMessage);
    }

    private String extractProviderMessageId(Header[] headers, String response) {
        String providerMessageId = null;

        if (getTemplateOutgoingResponse().hasHeaderMessageId()) {
            for (Header header : headers) {
                if (header.getName().equals(getTemplateOutgoingResponse().getHeaderMessageId())) {
                    providerMessageId = header.getValue();
                }
            }
            if (providerMessageId == null) {
                String message = String.format("Unable to find provider message id in '%s' header",
                        getTemplateOutgoingResponse().getHeaderMessageId());
                getMotechStatusMessage().alert(message);
            getLogger().error(message);
            }
        } else if (getTemplateOutgoingResponse().hasSingleSuccessMessageId()) {
            providerMessageId = getTemplateOutgoingResponse().extractSingleSuccessMessageId(response);
        }

        return providerMessageId;
    }

    @Override
    public void handle(OutgoingVxml vxml, String response, Header[] headers) {

        if (!getTemplateOutgoingResponse().hasSuccessResponse() ||
                getTemplateOutgoingResponse().checkSuccessResponse(response)) {

            String providerMessageId = extractProviderMessageId(headers, response);

            //todo: HIPAA concerns?
            getLogger().info(String.format("Sent messageId %s '%s' to %s", providerMessageId, messageForLog(vxml),
                    vxml.getRecipients().toString()));
            for (String recipient : vxml.getRecipients()) {
                getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, recipient, vxml.getMessage(), now(),
                        CallStatus.DISPATCHED, null, vxml.getMotechId(), providerMessageId, null));
            }
            getEvents().add(outboundEvent(CallEventSubjects.DISPATCHED, getConfig().getName(), vxml.getRecipients(),
                    vxml.getMessage(), vxml.getMotechId(), providerMessageId, null, null, null));

        } else {
            Integer failureCount = vxml.getFailureCount() + 1;

            String failureMessage = getTemplateOutgoingResponse().extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                    vxml.getRecipients(), vxml.getMessage(), vxml.getMotechId(), null, failureCount, null, null));
            getLogger().debug("Failed to send VXML: {}", failureMessage);
            for (String recipient : vxml.getRecipients()) {
                getAuditRecords().add(new CallRecord(getConfig().getName(), OUTBOUND, recipient, vxml.getMessage(), now(),
                        getConfig().retryOrAbortStatus(failureCount), null, vxml.getMotechId(), null, failureMessage));
            }
        }
    }
}
