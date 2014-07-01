package org.motechproject.vxml.web;

import org.motechproject.vxml.audit.CallRecords;
import org.motechproject.vxml.audit.VxmlRecordSearchCriteria;
import org.motechproject.vxml.audit.AuditService;
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
    public CallRecords getVxmlRecords(GridSettings settings) {
        CallRecords callRecords = new CallRecords();
        VxmlRecordSearchCriteria criteria = settings.toVxmlRecordSearchCriteria();

        if (!criteria.getCallDirections().isEmpty() && !criteria.getCallStatuses().isEmpty()) {
            callRecords = auditService.findAllVxmlRecords(criteria);
        }
        return callRecords;
    }
}
