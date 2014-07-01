package org.motechproject.vxml;

/**
 * Event subjects, mirrors CallStatus
 */
public final class CallEventSubjects {

    private CallEventSubjects() { }

    public static final String PENDING = "outbound_vxml_pending";
    public static final String RETRYING = "outbound_vxml_retrying";
    public static final String ABORTED = "outbound_vxml_aborted";
    public static final String SCHEDULED = "outbound_vxml_scheduled";
    public static final String DISPATCHED = "outbound_vxml_dispatched";
    public static final String DELIVERY_CONFIRMED = "outbound_vxml_delivery_confirmed";
    public static final String FAILURE_CONFIRMED = "outbound_vxml_failure_confirmed";
    public static final String SEND_VXML = "send_vxml";
    public static final String INBOUND_VXML = "inbound_vxml";
}
