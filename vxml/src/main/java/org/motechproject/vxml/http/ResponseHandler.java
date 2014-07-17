package org.motechproject.vxml.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.event.MotechEvent;
import org.motechproject.vxml.alert.MotechStatusMessage;
import org.motechproject.vxml.log.CallRecord;
import org.motechproject.vxml.configs.Config;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.templates.Response;
import org.motechproject.vxml.templates.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * figures out success or failure from the provider response
 */
public abstract class ResponseHandler {
    private Template template;
    private Config config;
    private Response templateOutgoingResponse;
    private List<MotechEvent> events = new ArrayList<MotechEvent>();
    private List<CallRecord> auditRecords = new ArrayList<CallRecord>();
    private Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private MotechStatusMessage motechStatusMessage;

    ResponseHandler(Template template, Config config, MotechStatusMessage motechStatusMessage) {
        this.template = template;
        this.config = config;
        this.motechStatusMessage = motechStatusMessage;
        templateOutgoingResponse = template.getOutgoing().getResponse();
    }

    public abstract void handle(OutgoingVxml vxml, String response, Header[] headers);

    public String messageForLog(OutgoingVxml vxml) {
        return vxml.getMessage().replace("\n", "\\n");
    }

    public List<MotechEvent> getEvents() {
        return events;
    }

    public List<CallRecord> getAuditRecords() {
        return auditRecords;
    }

    public Template getTemplate() {
        return template;
    }

    protected void setTemplate(Template template) {
        this.template = template;
    }

    protected Config getConfig() {
        return config;
    }

    protected void setConfig(Config config) {
        this.config = config;
    }

    protected Response getTemplateOutgoingResponse() {
        return templateOutgoingResponse;
    }

    protected Logger getLogger() {
        return logger;
    }

    public MotechStatusMessage getMotechStatusMessage() {
        return motechStatusMessage;
    }
}
