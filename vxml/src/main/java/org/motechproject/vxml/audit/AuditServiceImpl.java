package org.motechproject.vxml.audit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Reading and writing to the VXML audit log
 */
@Service
public class AuditServiceImpl implements AuditService {
    private CallRecordsDataService callRecordsDataService;
    private Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    public AuditServiceImpl(CallRecordsDataService callRecordsDataService) {
        this.callRecordsDataService = callRecordsDataService;
    }

    @Override
    public void log(CallRecord callRecord) {
        logger.info(callRecord.toString());
        callRecordsDataService.create(callRecord);
    }

    @Override
    public List<CallRecord> findAllVxmlRecords() {
        return callRecordsDataService.retrieveAll();
    }

    @Override
    public CallRecords findAllVxmlRecords(CallRecordSearchCriteria callRecordSearchCriteria) {
        List<CallRecord> recordList = (List<CallRecord>) executeQuery(callRecordSearchCriteria, false);
        return new CallRecords(recordList);
    }

    @Override
    public long countAllVxmlRecords(CallRecordSearchCriteria callRecordSearchCriteria) {
        return (long) executeQuery(callRecordSearchCriteria, true);
    }

    private Object executeQuery(CallRecordSearchCriteria callRecordSearchCriteria, boolean count) {
        Set<CallDirection> directions = callRecordSearchCriteria.getCallDirections();
        Set<CallStatus> statuses = callRecordSearchCriteria.getCallStatuses();
        Range<DateTime> timestampRange = callRecordSearchCriteria.getTimeRange();
        String config = asQuery(callRecordSearchCriteria.getConfig());
        String phoneNumber = asQuery(callRecordSearchCriteria.getPhoneNumber());
        String messageContent = asQuery(callRecordSearchCriteria.getMessageContent());
        String providerStatus = asQuery(callRecordSearchCriteria.getProviderStatus());
        String motechId = asQuery(callRecordSearchCriteria.getMotechId());
        String providerId = asQuery(callRecordSearchCriteria.getProviderId());

        if (count) {
            return callRecordsDataService.countByCriteria(config, directions, phoneNumber, messageContent,
                    timestampRange, statuses, providerStatus, motechId, providerId);
        } else {
            boolean reverse = "desc".equalsIgnoreCase(callRecordSearchCriteria.getSortDirection());
            Order order = new Order(callRecordSearchCriteria.getSortColumn(), (reverse) ? Order.Direction.ASC :
                    Order.Direction.DESC);
            QueryParams queryParams = new QueryParams(callRecordSearchCriteria.getPage(), callRecordSearchCriteria.getRows(), order);
            return callRecordsDataService.findByCriteria(
                    config, directions, phoneNumber, messageContent, timestampRange, statuses,
                    providerStatus, motechId, providerId, queryParams);
        }
    }

    private String asQuery(String value) {
        return StringUtils.isNotBlank(value) ? String.format(".*%s.*", value) : value;
    }
}
