package org.motechproject.vxml.web;

import org.motechproject.vxml.log.LogService;
import org.motechproject.vxml.log.CallRecordSearchCriteria;
import org.motechproject.vxml.log.CallRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Returns a list of vxml log records to the UI (which queried the server @
 * {server}/motech-platform-server/module/vxml/log
 */
@Controller
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    @ResponseBody
    public CallRecords getCallRecords(CallRecordSearchCriteria callRecordSearchCriteria) {
        CallRecords callRecords = new CallRecords();

        if (!callRecordSearchCriteria.getCallDirections().isEmpty() && !callRecordSearchCriteria.getCallStatuses().isEmpty()) {
            callRecords = logService.findAllCallRecords(callRecordSearchCriteria);
        }

        return callRecords;
    }
}
