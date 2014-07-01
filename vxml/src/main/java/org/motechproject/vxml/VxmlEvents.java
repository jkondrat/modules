package org.motechproject.vxml;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.motechproject.commons.date.util.DateUtil.now;


/**
 * MotechEvent Helper class
 */
public final class VxmlEvents {

    private VxmlEvents() { }

    public static MotechEvent inboundEvent(String config, String sender, String recipient, String message,
                                           String providerMessageId, DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(VxmlEventParams.CONFIG, config);
        params.put(VxmlEventParams.SENDER, sender);
        params.put(VxmlEventParams.RECIPIENT, recipient);
        params.put(VxmlEventParams.MESSAGE, message);
        params.put(VxmlEventParams.PROVIDER_MESSAGE_ID, providerMessageId);
        params.put(VxmlEventParams.TIMESTAMP_DATETIME, timestamp);

        DateTime dtUTC = new DateTime(timestamp, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        String time = String.format("%02d:%02d Z", dtUTC.getHourOfDay(), dtUTC.getMinuteOfHour());
        params.put(VxmlEventParams.TIMESTAMP_TIME, time);

        return new MotechEvent(CallEventSubjects.INBOUND_VXML, params);
    }

    public static MotechEvent outboundEvent(String subject, String config, //NO CHECKSTYLE ParameterNumber
                                            List<String> recipients, String message, String motechId,
                                            String providerMessageId, Integer failureCount, String providerStatus,
                                            DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(VxmlEventParams.CONFIG, config);
        params.put(VxmlEventParams.RECIPIENTS, recipients);
        params.put(VxmlEventParams.MESSAGE, message);
        params.put(VxmlEventParams.MOTECH_ID, motechId);
        if (providerMessageId != null) {
            params.put(VxmlEventParams.PROVIDER_MESSAGE_ID, providerMessageId);
        }
        if (failureCount != null) {
            params.put(VxmlEventParams.FAILURE_COUNT, failureCount);
        } else {
            params.put(VxmlEventParams.FAILURE_COUNT, 0);
        }
        if (providerStatus != null) {
            params.put(VxmlEventParams.PROVIDER_STATUS, providerStatus);
        }
        if (timestamp == null) {
            params.put(VxmlEventParams.TIMESTAMP, now());
        } else {
            params.put(VxmlEventParams.TIMESTAMP, timestamp);
        }
        return new MotechEvent(subject, params);
    }
}
