package org.motechproject.vxml.audit;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.Constants.Operators.MATCHES;

/**
 * Used to query the audit records in the database
 */
public interface VxmlRecordsDataService extends MotechDataService<CallRecord> {

    @Lookup
    List<CallRecord> findByCriteria(@LookupField(name = "config", customOperator = MATCHES) String config, //NO CHECKSTYLE ParameterNumber
                                   @LookupField(name = "callDirection") Set<CallDirection> directions,
                                   @LookupField(name = "phoneNumber", customOperator = MATCHES) String phoneNumber,
                                   @LookupField(name = "messageContent", customOperator = MATCHES) String messageContent,
                                   @LookupField(name = "timestamp") Range<DateTime> timestamp,
                                   @LookupField(name = "callStatus") Set<CallStatus> callStatuses,
                                   @LookupField(name = "providerStatus", customOperator = MATCHES) String providerStatus,
                                   @LookupField(name = "motechId", customOperator = MATCHES) String motechId,
                                   @LookupField(name = "providerId", customOperator = MATCHES) String providerId,
                                   @LookupField(name = "errorMessage", customOperator = MATCHES) String errorMessage,
                                   QueryParams queryParams);

    long countFindByCriteria(@LookupField(name = "config", customOperator = MATCHES) String config, //NO CHECKSTYLE ParameterNumber
                             @LookupField(name = "callDirection") Set<CallDirection> directions,
                             @LookupField(name = "phoneNumber", customOperator = MATCHES) String phoneNumber,
                             @LookupField(name = "messageContent", customOperator = MATCHES) String messageContent,
                             @LookupField(name = "timestamp") Range<DateTime> timestamp,
                             @LookupField(name = "callStatus") Set<CallStatus> callStatuses,
                             @LookupField(name = "providerStatus", customOperator = "matches()") String providerStatus,
                             @LookupField(name = "motechId", customOperator = MATCHES) String motechId,
                             @LookupField(name = "providerId", customOperator = MATCHES) String providerId,
                             @LookupField(name = "errorMessage", customOperator = MATCHES) String errorMessage);

}
