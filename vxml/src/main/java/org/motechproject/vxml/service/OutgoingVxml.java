package org.motechproject.vxml.service;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.vxml.VxmlEventParams;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Represents an outgoing VXML
 */
public class OutgoingVxml {
    /**
     * One or more recipients
     */
    private List<String> recipients;
    /**
     * The content of the message to send
     */
    private String message;
    /**
     * If specified, use this config to send the VXML, otherwise use the default config
     */
    private String config;
    /**
     * If specified will schedule the message for future delivery using the MOTECH scheduler
     */
    private DateTime deliveryTime;
    /**
     * MOTECH specific message GUID
     */
    private String motechId;
    /**
     * Provider specific message id
     */
    private String providerId;
    /**
     * Internal failure counter
     */
    private Integer failureCount = 0;

    public OutgoingVxml() {
    }

    public OutgoingVxml(MotechEvent event) {
        Map<String, Object> params = event.getParameters();
        config = (String) params.get(VxmlEventParams.CONFIG);
        recipients = (List<String>) params.get(VxmlEventParams.RECIPIENTS);
        message = (String) params.get(VxmlEventParams.MESSAGE);
        deliveryTime = (DateTime) params.get(VxmlEventParams.DELIVERY_TIME);
        if (params.containsKey(VxmlEventParams.FAILURE_COUNT)) {
            failureCount = (Integer) params.get(VxmlEventParams.FAILURE_COUNT);
        }
        motechId = (String) params.get(VxmlEventParams.MOTECH_ID);
        providerId = (String) params.get(VxmlEventParams.PROVIDER_MESSAGE_ID);
    }

    public OutgoingVxml(String config, List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.config = config;
        this.deliveryTime = deliveryTime;
    }

    public OutgoingVxml(String config, List<String> recipients, String message) {
        this.recipients = recipients;
        this.message = message;
        this.config = config;
    }

    public OutgoingVxml(List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.deliveryTime = deliveryTime;
    }

    public OutgoingVxml(List<String> recipients, String message) {

        this.recipients = recipients;
        this.message = message;
    }

    public String getConfig() {
        return config;
    }

    public Boolean hasConfig() {
        return isNotBlank(config);
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getDeliveryTime() {
        return deliveryTime;
    }

    public Boolean hasDeliveryTime() {
        return deliveryTime != null;
    }


    public void setDeliveryTime(DateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Boolean hasMotechId() {
        return isNotBlank(motechId);
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

    @Override
    public int hashCode() {
        return Objects.hash(recipients, message, deliveryTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final OutgoingVxml other = (OutgoingVxml) obj;

        return Objects.equals(this.config, other.config)
                && Objects.equals(this.recipients, other.recipients)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.deliveryTime, other.deliveryTime)
                && Objects.equals(this.failureCount, other.failureCount)
                && Objects.equals(this.motechId, other.motechId)
                && Objects.equals(this.providerId, other.providerId);
    }

    @Override
    public String toString() {
        return "OutgoingVxml{" +
                "recipients=" + recipients +
                ", message='" + message + '\'' +
                ", config='" + config + '\'' +
                ", deliveryTime=" + deliveryTime +
                ", failureCount=" + failureCount +
                ", motechId='" + motechId + '\'' +
                ", providerId='" + providerId + '\'' +
                '}';
    }
}
