package org.motechproject.vxml.configs;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.vxml.CallEventSubjects;
import org.motechproject.vxml.log.CallStatus;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    private static final int failureCount = 3;
    private Config config;

    @Before
    public void setup() {
        config = new Config();
        config.setMaxRetries(failureCount);
    }

    @Test
    public void shouldReturnRetryThenAbortSubject() {
        assertEquals(CallEventSubjects.RETRYING, config.retryOrAbortSubject(failureCount - 1));
        assertEquals(CallEventSubjects.ABORTED, config.retryOrAbortSubject(failureCount));
    }

    @Test
    public void shouldReturnRetryThenAbortStatus() {
        assertEquals(CallStatus.RETRYING, config.retryOrAbortStatus(failureCount - 1));
        assertEquals(CallStatus.ABORTED, config.retryOrAbortStatus(failureCount));
    }
}
