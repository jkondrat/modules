package org.motechproject.vxml.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * VXML audit records from the database
 */
public class CallRecords implements Serializable {

    private static final long serialVersionUID = -1584588569625856505L;
    private List<CallRecord> records;

    public CallRecords() {
        this.records = new ArrayList<>();
    }

    public CallRecords(List<CallRecord> records) {
        this.records = records;
    }

    public List<CallRecord> getRows() {
        return records;
    }

    public Long getRecords() {
        return (long) records.size();
    }
}
