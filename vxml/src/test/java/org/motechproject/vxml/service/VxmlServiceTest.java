package org.motechproject.vxml.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.vxml.log.LogService;
import org.motechproject.vxml.templates.TemplateReader;

import java.util.Arrays;

import static org.mockito.MockitoAnnotations.initMocks;

public class VxmlServiceTest {
    @Mock
    private EventRelay eventRelay;
    @Mock
    MotechSchedulerService schedulerService;
    @Mock
    TemplateReader templateReader;
    @Mock
    LogService logService;

    private VxmlService vxmlSender;

    @Before
    public void setUp() {
        initMocks(this);
        //when(templateReader.getTemplates()).thenReturn(new Templates(settings, new ArrayList<Template>()));

        SettingsFacade settings = new SettingsFacade();
        //settings.saveConfigProperties("ivr.properties", ivrProperties);

        //vxmlSender = new VxmlServiceImpl(settings, eventRelay, schedulerService, templateReader, vxmlAuditService);
    }
    @Test
    public void shouldSendVxml() throws Exception {
        OutgoingVxml outgoingVxml = new OutgoingVxml(Arrays.asList(new String[]{"+12065551212"}), "sample message");
        //vxmlSender.send(outgoingVxml);
    }
}
