package org.motechproject.vxml.web;

import org.motechproject.vxml.audit.AuditService;
import org.motechproject.vxml.audit.CallRecordSearchCriteria;
import org.motechproject.vxml.audit.CallRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Returns a list of vxml audit records to the UI (which queried the server @
 * {server}/motech-platform-server/module/vxml/log
 */
@Controller
public class LogController {

    @Autowired
    private AuditService auditService;

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    @ResponseBody
    public CallRecords getVxmlRecords(CallRecordSearchCriteria callRecordSearchCriteria) {
        CallRecords callRecords = new CallRecords();

        if (!callRecordSearchCriteria.getCallDirections().isEmpty() && !callRecordSearchCriteria.getCallStatuses().isEmpty()) {
            callRecords = auditService.findAllVxmlRecords(callRecordSearchCriteria);
        }

        return callRecords;
    }
}
