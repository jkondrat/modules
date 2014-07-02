package org.motechproject.vxml.log;

import java.util.List;

/**
 * Reading and writing to the VXML call log
 */
public interface LogService {

    void log(CallRecord callRecord);

    List<CallRecord> findAllCallRecords();

    CallRecords findAllCallRecords(CallRecordSearchCriteria callRecordSearchCriteria);

    long countAllCallRecords(CallRecordSearchCriteria callRecordSearchCriteria);
}
