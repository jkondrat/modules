package org.motechproject.vxml.configs;

import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.log.CallStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * A connection to a particular VXML provider, there may be more than one config for a given provider and/or multiple
 * connections to multiple providers. But realistically, most implementations will have one config.
 */
public class Config {
    private String name;
    private Integer maxRetries;
    private Boolean excludeLastFooter;
    private String splitHeader;
    private String splitFooter;
    private String templateName;

    private List<ConfigProp> props = new ArrayList<ConfigProp>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getExcludeLastFooter() {
        return excludeLastFooter;
    }

    public void setExcludeLastFooter(Boolean excludeLastFooter) {
        this.excludeLastFooter = excludeLastFooter;
    }

    public String getSplitHeader() {
        return splitHeader;
    }

    public void setSplitHeader(String splitHeader) {
        this.splitHeader = splitHeader;
    }

    public String getSplitFooter() {
        return splitFooter;
    }

    public void setSplitFooter(String splitFooter) {
        this.splitFooter = splitFooter;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<ConfigProp> getProps() {
        return props;
    }

    public void setProps(List<ConfigProp> props) {
        this.props = props;
    }

    public CallStatus retryOrAbortStatus(Integer failureCount) {
        if (failureCount < maxRetries) {
            return CallStatus.RETRYING;
        }
        return CallStatus.ABORTED;
    }

    public String retryOrAbortSubject(Integer failureCount) {
        if (failureCount < maxRetries) {
            return CallEventSubjects.RETRYING;
        }
        return CallEventSubjects.ABORTED;
    }
}
