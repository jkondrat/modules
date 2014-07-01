package org.motechproject.vxml.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.http.VxmlHttpService;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.service.VxmlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When another module sends an VXML, it calls VxmlService.send, which in turn sends one or more SEND_VXML events which
 * are handled here and passed straight through to to VxmlHttpService.send
 */
@Service
public class SendVxmlEventHandler {

    private VxmlHttpService vxmlHttpService;
    private Logger logger = LoggerFactory.getLogger(SendVxmlEventHandler.class);
    private VxmlService vxmlService;

    @Autowired
    public SendVxmlEventHandler(VxmlHttpService vxmlHttpService, VxmlService vxmlService) {
        this.vxmlHttpService = vxmlHttpService;
        this.vxmlService = vxmlService;
    }

    @MotechListener (subjects = { CallEventSubjects.SEND_VXML })
    public void handleExternal(MotechEvent event) {
        logger.info("Handling external event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        vxmlService.send(new OutgoingVxml(event));
    }

    @MotechListener (subjects = { CallEventSubjects.PENDING, CallEventSubjects.SCHEDULED, CallEventSubjects.RETRYING })
    public void handleInternal(MotechEvent event) {
        logger.info("Handling internal event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        vxmlHttpService.send(new OutgoingVxml(event));
    }
}

