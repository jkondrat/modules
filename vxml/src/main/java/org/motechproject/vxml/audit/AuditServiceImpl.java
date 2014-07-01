package org.motechproject.vxml.audit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.motechproject.commons.api.MotechEnumUtils.toEnumSet;

/**
 * Reading and writing to the VXML audit log
 */
@Service
public class AuditServiceImpl implements AuditService {
    private VxmlRecordsDataService vxmlRecordsDataService;
    private Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    public AuditServiceImpl(VxmlRecordsDataService vxmlRecordsDataService) {
        this.vxmlRecordsDataService = vxmlRecordsDataService;
    }

    @Override
    public void log(CallRecord callRecord) {
        logger.info(callRecord.toString());
        vxmlRecordsDataService.create(callRecord);
    }

    @Override
    public List<CallRecord> findAllVxmlRecords() {
        return vxmlRecordsDataService.retrieveAll();
    }

    @Override
    public CallRecords findAllVxmlRecords(VxmlRecordSearchCriteria criteria) {
        List<CallRecord> recordList = (List<CallRecord>) executeQuery(criteria, false);
        return new CallRecords(recordList.size(), recordList);
    }

    @Override
    public long countAllVxmlRecords(VxmlRecordSearchCriteria criteria) {
        return (long) executeQuery(criteria, true);
    }

    private Object executeQuery(VxmlRecordSearchCriteria criteria, boolean count) {
        Set<String> directions = criteria.getCallDirections();
        Set<CallDirection> directionsEnum = toEnumSet(CallDirection.class, directions);

        Set<String> statuses = criteria.getCallStatuses();
        Set<CallStatus> statusesEnum = toEnumSet(CallStatus.class, statuses);

        Range<DateTime> timestampRange = criteria.getTimestampRange();

        String config = asQuery(criteria.getConfig());
        String phoneNumber = asQuery(criteria.getPhoneNumber());
        String messageContent = asQuery(criteria.getMessageContent());
        String providerStatus = asQuery(criteria.getProviderStatus());
        String motechId = asQuery(criteria.getMotechId());
        String providerId = asQuery(criteria.getProviderId());
        String errorMessage = asQuery(criteria.getErrorMessage());

        QueryParams queryParams = criteria.getQueryParams();

        if (count) {
            return vxmlRecordsDataService.countFindByCriteria(config, directionsEnum, phoneNumber, messageContent,
                    timestampRange, statusesEnum, providerStatus, motechId, providerId, errorMessage);
        } else {
            return vxmlRecordsDataService.findByCriteria(
                    config, directionsEnum, phoneNumber, messageContent, timestampRange, statusesEnum,
                    providerStatus, motechId, providerId, errorMessage, queryParams);
        }
    }

    private String asQuery(String value) {
        return StringUtils.isNotBlank(value) ? String.format(".*%s.*", value) : value;
    }
}
