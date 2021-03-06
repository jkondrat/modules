package org.motechproject.ivr.domain;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;

import java.io.Serializable;

import static org.motechproject.commons.date.util.DateUtil.setTimeZoneUTC;

/**
 * Represents IVR event like DTMF key press, Dial
 * @see IvrEvent
 */
public class CallEventLog implements Serializable {
    private static final long serialVersionUID = -5399759051930894664L;

    private String name;
    private DateTime timeStamp;
    private CallEventCustomData callEventCustomData = new CallEventCustomData();

    private CallEventLog() {
    }

    public CallEventLog(String name) {
        this(name, DateUtil.now());
    }

    private CallEventLog(String name, DateTime timeStamp) {
        this.name = name;
        this.timeStamp = timeStamp;
    }

    /**
     * Factory method to create new dial event.
     * @return Dial event
     */
    public static CallEventLog newDialEvent() {
        return new CallEventLog("Dial", DateUtil.now());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getTimeStamp() {
        return setTimeZoneUTC(timeStamp);
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Get additional data for call event such as audio played etc.
     * @return Data map with key value pair
     *
     */
    public CallEventCustomData getData() {
        return callEventCustomData;
    }

    /**
     * Set additional data for call event such as audio played etc.
     * @param callEventCustomData
     */
    public void setData(CallEventCustomData callEventCustomData) {
        this.callEventCustomData = callEventCustomData;
    }

    /**
     * Add key value pair to additional data in call event.
     * It can be used to store information related to IVR event like audio played etc.
     * @param key
     * @param value
     */
    public void appendData(String key, String value) {
        callEventCustomData.put(key, value);
    }
}
