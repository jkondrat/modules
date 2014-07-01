package org.motechproject.vxml.audit;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;

import java.util.HashSet;
import java.util.Set;

import static org.motechproject.commons.api.MotechEnumUtils.toStringSet;


/**
 * Helper used to generate lucene query from log filter UI
 */
public class VxmlRecordSearchCriteria {

    private Set<CallDirection> callDirections = new HashSet<>();
    private String config;
    private String phoneNumber;
    private String messageContent;
    private Range<DateTime> timestampRange;
    private String providerStatus;
    private Set<CallStatus> callStatuses = new HashSet<>();
    private String motechId;
    private String providerId;
    private String errorMessage;
    private QueryParams queryParams;

    public VxmlRecordSearchCriteria withCallDirections(Set<CallDirection> callDirections) {
        this.callDirections.addAll(callDirections);
        return this;
    }

    public VxmlRecordSearchCriteria withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public VxmlRecordSearchCriteria withConfig(String config) {
        this.config = config;
        return this;
    }

    public VxmlRecordSearchCriteria withMotechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public VxmlRecordSearchCriteria withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public VxmlRecordSearchCriteria withMessageContent(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    public VxmlRecordSearchCriteria withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public VxmlRecordSearchCriteria withTimestamp(DateTime timestamp) {
        this.timestampRange = new Range<>(timestamp, timestamp);
        return this;
    }

    public VxmlRecordSearchCriteria withTimestampRange(Range<DateTime> timestampRange) {
        this.timestampRange = timestampRange;
        return this;
    }

    public VxmlRecordSearchCriteria withProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
        return this;
    }

    public VxmlRecordSearchCriteria withCallstatuses(Set<CallStatus> callStatuses) {
        this.callStatuses.addAll(callStatuses);
        return this;
    }

    public VxmlRecordSearchCriteria withQueryParams(QueryParams queryParam) {
        this.queryParams = queryParam;
        return this;
    }

    // Getters

    public Set<String> getCallDirections() {
        return toStringSet(callDirections);
    }

    public String getConfig() {
        return config;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Range<DateTime> getTimestampRange() {
        return timestampRange;
    }

    public Set<String> getCallStatuses() {
        return toStringSet(callStatuses);
    }

    public String getMotechId() {
        return motechId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    @Override
    public String toString() {
        return "VxmlRecordSearchCriteria{" +
                "callDirections=" + callDirections +
                ", config='" + config + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", timestampRange=" + timestampRange +
                ", providerStatus='" + providerStatus + '\'' +
                ", callStatuses=" + callStatuses +
                ", motechId='" + motechId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}
