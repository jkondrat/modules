package org.motechproject.vxml.audit;

import java.util.List;

/**
 * Reading and writing to the VXML audit log
 */
public interface AuditService {

    void log(CallRecord callRecord);

    List<CallRecord> findAllVxmlRecords();

    CallRecords findAllVxmlRecords(VxmlRecordSearchCriteria criteria);

    long countAllVxmlRecords(VxmlRecordSearchCriteria criteria);
}
