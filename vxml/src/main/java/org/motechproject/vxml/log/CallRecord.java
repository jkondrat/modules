package org.motechproject.vxml.log;

//todo: motechTimestanp & providerTimestamp instead of just timestamp?
//todo: 'senderNumber' & 'recipientNumber' instead of 'phoneNumber'?

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.annotations.Entity;

/**
 * VXML log record for the database
 */
@Entity
public class CallRecord {
    private String config;
    private CallDirection callDirection;
    private String phoneNumber;
    private String messageContent;
    private DateTime timestamp;
    private CallStatus callStatus;
    private String providerStatus;
    private String motechId;
    private String providerId;
    private String errorMessage;

    public CallRecord() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    public CallRecord(String config, CallDirection callDirection, String number,  //NO CHECKSTYLE ParameterNumber
                      String message, DateTime timestamp, CallStatus callStatus, String providerStatus,
                      String motechId, String providerId, String errorMessage) {
        this.config = config;
        this.callDirection = callDirection;
        this.phoneNumber = number;
        this.messageContent = message;
        this.timestamp = timestamp;
        this.callStatus = callStatus;
        this.providerStatus = providerStatus;
        this.motechId = motechId;
        this.providerId = providerId;
        this.errorMessage = errorMessage;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public CallDirection getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public DateTime getTimestamp() {
        return DateUtil.setTimeZoneUTC(timestamp);
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
