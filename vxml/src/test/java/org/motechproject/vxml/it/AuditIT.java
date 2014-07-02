package org.motechproject.vxml.it;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.vxml.log.CallDirection;
import org.motechproject.vxml.log.CallRecord;
import org.motechproject.vxml.log.CallRecordsDataService;
import org.motechproject.vxml.log.CallStatus;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * TODO
 */

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class AuditIT extends BasePaxIT {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private CallRecordsDataService dataService;

    @Test
    public void verifyAuditIsFunctional() throws Exception {

        logger.info("verifyAuditIsFunctional >>> start");

        dataService.deleteAll();
        dataService.create(new CallRecord("config", CallDirection.INBOUND, "123", "message1", DateTime.now(),
                CallStatus.RECEIVED, "provider_status_foo", "motech_id_1", "provider_id_a", ""));
        dataService.create(new CallRecord("config", CallDirection.INBOUND, "456", "message2", DateTime.now(),
                CallStatus.RECEIVED, "provider_status_foo", "motech_id_2", "provider_id_b", ""));

        List<CallRecord> callRecords = dataService.findByCriteria(null, null, "123", null, null, null, null, null, null,
                null);

        assertEquals(callRecords.size(), 1);
        assertEquals("message1", callRecords.get(0).getMessageContent());

        logger.info("verifyAuditIsFunctional <<< stop");
    }
}
