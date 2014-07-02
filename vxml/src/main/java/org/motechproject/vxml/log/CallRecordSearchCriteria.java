package org.motechproject.vxml.log;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Models the call log filter settings UI
 */
public class CallRecordSearchCriteria {

    private Integer rows;
    private Integer page;
    private String sortColumn;
    private String sortDirection;
    private String config;
    private String phoneNumber;
    private String messageContent;
    private String timeFrom;
    private String timeTo;
    private String callStatus;
    private String providerStatus;
    private String callDirection;
    private String motechId;
    private String providerId;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
    }

    public String getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(String callDirection) {
        this.callDirection = callDirection;
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

    public Set<CallDirection> getCallDirections() {
        Set<CallDirection> callDirections = new HashSet<>();
        String[] callDirectionList = callDirection.split(",");
        for (String type : callDirectionList) {
            if (!type.isEmpty()) {
                callDirections.add(CallDirection.valueOf(type));
            }
        }
        return callDirections;
    }

    public Set<CallStatus> getCallStatuses() {
        Set<CallStatus> statusList = new HashSet<>();
        String[] statuses = callStatus.split(",");
        for (String status : statuses) {
            if (!status.isEmpty()) {
                statusList.add(CallStatus.valueOf(status));
            }
        }
        return statusList;
    }

    public Range<DateTime> getTimeRange() {
        DateTime from;
        DateTime to;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(timeFrom)) {
            from = formatter.parseDateTime(timeFrom);
        } else {
            from = new DateTime(0);
        }
        if (StringUtils.isNotBlank(timeTo)) {
            to = formatter.parseDateTime(timeTo);
        } else {
            to = DateTime.now();
        }
        return new Range<>(from, to);
    }

    @Override
    public String toString() {
        return "GridSettings{" +
                "rows=" + rows +
                ", page=" + page +
                ", sortColumn='" + sortColumn + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", config='" + config + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", timeFrom='" + timeFrom + '\'' +
                ", timeTo='" + timeTo + '\'' +
                ", callStatus='" + callStatus + '\'' +
                ", providerStatus='" + providerStatus + '\'' +
                ", callDirection='" + callDirection + '\'' +
                ", motechId='" + motechId + '\'' +
                ", providerId='" + providerId + '\'' +
                '}';
    }
}
