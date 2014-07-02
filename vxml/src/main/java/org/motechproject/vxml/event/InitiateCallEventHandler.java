package org.motechproject.vxml.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.http.HttpHelper;
import org.motechproject.vxml.service.OutgoingVxml;
import org.motechproject.vxml.service.VxmlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When another module sends an VXML, it calls VxmlService.send, which in turn sends one or more INITIATE_VXML_CALL events which
 * are handled here and passed straight through to to VxmlHttpService.send
 */
@Service
public class InitiateCallEventHandler {

    private HttpHelper httpHelper;
    private Logger logger = LoggerFactory.getLogger(InitiateCallEventHandler.class);
    private VxmlService vxmlService;

    @Autowired
    public InitiateCallEventHandler(HttpHelper httpHelper, VxmlService vxmlService) {
        this.httpHelper = httpHelper;
        this.vxmlService = vxmlService;
    }

    @MotechListener (subjects = { CallEventSubjects.INITIATE_VXML_CALL })
    public void handleExternal(MotechEvent event) {
        logger.info("Handling external event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        vxmlService.send(new OutgoingVxml(event));
    }

    @MotechListener (subjects = { CallEventSubjects.PENDING, CallEventSubjects.SCHEDULED, CallEventSubjects.RETRYING })
    public void handleInternal(MotechEvent event) {
        logger.info("Handling internal event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        httpHelper.send(new OutgoingVxml(event));
    }
}

