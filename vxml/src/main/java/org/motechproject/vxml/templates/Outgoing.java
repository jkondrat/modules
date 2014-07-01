package org.motechproject.vxml.templates;

import org.motechproject.server.config.SettingsFacade;

/**
 * How providers deal with outgoing messages
 */
public class Outgoing {
    private static final String VXML_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES = "vxml.default.millisecond_between_messages";
    private static final String VXML_DEFAULT_MAX_VXML_SIZE = "vxml.default.max_vxml_size";
    private static final String VXML_DEFAULT_MAX_RECIPIENT = "vxml.default.max_recipient";
    private static final String VXML_DEFAULT_RECIPIENT_SEPARATOR = "vxml.default.recipient_separator";

    private Request request;
    private Response response;
    private Boolean hasAuthentication;
    private Boolean exponentialBackOffRetries;
    private Integer millisecondsBetweenMessages;
    private Integer maxVxmlSize;
    private Integer maxRecipient;
    private String recipientSeparator;
    private Integer defaultMillisecondsBetweenMessages;
    private Integer defaultMaxVxmlSize;
    private Integer defaultMaxRecipient;
    private String defaultRecipientSeparator;

    public void readDefaults(SettingsFacade settingsFacade) {
        defaultMillisecondsBetweenMessages = Integer.valueOf(settingsFacade.getProperty(
                VXML_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES));
        defaultMaxVxmlSize = Integer.valueOf(settingsFacade.getProperty(VXML_DEFAULT_MAX_VXML_SIZE));
        defaultMaxRecipient = Integer.valueOf(settingsFacade.getProperty(VXML_DEFAULT_MAX_RECIPIENT));
        defaultRecipientSeparator = settingsFacade.getProperty(VXML_DEFAULT_RECIPIENT_SEPARATOR);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Boolean hasAuthentication() {
        return hasAuthentication;
    }

    public void setHasAuthentication(Boolean hasAuthentication) {
        this.hasAuthentication = hasAuthentication;
    }

    public Integer getMillisecondsBetweenMessages() {
        if (millisecondsBetweenMessages == null) {
            millisecondsBetweenMessages = defaultMillisecondsBetweenMessages;
        }
        return millisecondsBetweenMessages;
    }

    public void setMillisecondsBetweenMessages(Integer millisecondsBetweenMessages) {
        this.millisecondsBetweenMessages = millisecondsBetweenMessages;
    }

    public Boolean getExponentialBackOffRetries() {
        return exponentialBackOffRetries;
    }

    public void setExponentialBackOffRetries(Boolean exponentialBackOffRetries) {
        this.exponentialBackOffRetries = exponentialBackOffRetries;
    }

    public Integer getMaxVxmlSize() {
        if (maxVxmlSize == null) {
            maxVxmlSize = defaultMaxVxmlSize;
        }
        return maxVxmlSize;
    }

    public void setMaxVxmlSize(Integer maxVxmlSize) {
        this.maxVxmlSize = maxVxmlSize;
    }

    public Integer getMaxRecipient() {
        if (maxRecipient == null) {
            maxRecipient = defaultMaxRecipient;
        }
        return maxRecipient;
    }

    public void setMaxRecipient(Integer maxRecipient) {
        this.maxRecipient = maxRecipient;
    }

    public String getRecipientSeparator() {
        if (recipientSeparator == null) {
            recipientSeparator = defaultRecipientSeparator;
        }
        return recipientSeparator;
    }

    public void setRecipientSeparator(String recipientSeparator) {
        this.recipientSeparator = recipientSeparator;
    }

    @Override
    public String toString() {
        return "Outgoing{" +
                "request=" + request +
                ", response=" + response +
                ", hasAuthentication=" + hasAuthentication +
                ", millisecondsBetweenMessages=" + millisecondsBetweenMessages +
                ", exponentialBackOffRetries=" + exponentialBackOffRetries +
                ", maxVxmlSize=" + maxVxmlSize +
                ", maxRecipient=" + maxRecipient +
                ", recipientSeparator='" + recipientSeparator + '\'' +
                '}';
    }
}
