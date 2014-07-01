package org.motechproject.vxml.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * VXML audit records from the database
 */
public class CallRecords implements Serializable {

    private static final long serialVersionUID = -1584588569625856505L;
    private int count;
    private List<CallRecord> records;

    public CallRecords() {
        this.count = 0;
        this.records = new ArrayList<>();
    }

    public CallRecords(int count, List<CallRecord> records) {
        this.count = count;
        this.records = records;
    }

    public int getCount() {
        return count;
    }

    public List<CallRecord> getRecords() {
        return records;
    }

    public List<CallRecord> getRows() {
        return getRecords();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRecords(List<CallRecord> records) {
        this.records = records;
    }
}
