package org.motechproject.vxml.web;

import org.motechproject.vxml.service.VxmlService;
import org.motechproject.vxml.service.OutgoingVxml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * handles requests to {server}/motech-platform-server/module/vxml/outgoing
 * This is how the "Start outgoing call" dialog sends a message
 */
@Controller
public class OutgoingController {
    private VxmlService vxmlService;

    @Autowired
    public OutgoingController(VxmlService vxmlService) {
        this.vxmlService = vxmlService;
    }

    @RequestMapping(value = "/outgoing", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String sendVxml(@RequestBody OutgoingVxml outgoingVxml) {
        vxmlService.send(outgoingVxml);
        return String.format("Outgoing VXML call to %s using the %s config was added to the message queue.",
                outgoingVxml.getRecipients().toString(), outgoingVxml.getConfig());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
