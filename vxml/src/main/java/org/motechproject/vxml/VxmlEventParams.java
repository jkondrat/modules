package org.motechproject.vxml;

/**
 * Possible Vxml Event payloads (ie: params)
 */
public final class VxmlEventParams {

    private VxmlEventParams() { }

    /**
     * Config that was used for this message
     */
    public static final String CONFIG = "config";
    /**
     * list of recipients (phone numbers)
     */
    public static final String RECIPIENTS = "recipients";
    /**
     * incoming VXML recipient (phone number)
     */
    public static final String RECIPIENT = "recipient";
    /**
     * incoming VXML sender  (phone number)
     */
    public static final String SENDER = "sender";
    /**
     * time at which this VXML should be sent
     */
    public static final String DELIVERY_TIME = "delivery_time";
    /**
     * date & time when this event happened
     */
    public static final String TIMESTAMP = "timestamp";
    /**
     * date & time when this event happened
     */
    public static final String TIMESTAMP_DATETIME = "timestamp_datetime";
    /**
     * UTC time when this event happened
     */
    public static final String TIMESTAMP_TIME = "timestamp_time";
    /**
     * the text content of the VXML message
     */
    public static final String MESSAGE = "message";
    /**
     * internal VXML failure counter
     */
    public static final String FAILURE_COUNT = "failure_count";
    /**
     * MOTECH unique message id
     */
    public static final String MOTECH_ID = "motech_id";
    /**
     * provider unique message id
     */
    public static final String PROVIDER_MESSAGE_ID = "provider_message_id";
    /**
     * provider provided VXML delivery status, sometimes holds more information than what MOTECH models
     */
    public static final String PROVIDER_STATUS = "provider_status";
}
