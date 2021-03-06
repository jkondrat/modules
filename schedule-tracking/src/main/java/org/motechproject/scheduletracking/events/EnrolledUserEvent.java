package org.motechproject.scheduletracking.events;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduletracking.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.events.constants.EventSubjects;

import java.util.HashMap;
import java.util.Map;

public class EnrolledUserEvent {
    private String externalId;
    private String scheduleName;
    private Time preferredAlertTime;
    private LocalDate referenceDate;
    private Time referenceTime;
    private LocalDate enrollmentDate;
    private Time enrollmentTime;
    private String startingMilestoneName;

    public EnrolledUserEvent(String externalId, String scheduleName, Time preferredAlertTime, DateTime referendeDateTime, DateTime enrollmentDateTime, String startingMilestoneName) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.preferredAlertTime = preferredAlertTime;
        if (referendeDateTime == null) {
            this.referenceDate = null;
            this.referenceTime = null;
        } else {
            this.referenceDate = referendeDateTime.toLocalDate();
            this.referenceTime = new Time(referendeDateTime.toLocalTime());
        }
        if (enrollmentDateTime == null) {
            this.enrollmentDate = null;
            this.enrollmentTime = null;
        } else {
            this.enrollmentDate = enrollmentDateTime.toLocalDate();
            this.enrollmentTime = new Time(enrollmentDateTime.toLocalTime());
        }

        this.startingMilestoneName = startingMilestoneName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public Time getReferenceTime() {
        return referenceTime;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public Time getEnrollmentTime() {
        return enrollmentTime;
    }

    public String getStartingMilestoneName() {
        return startingMilestoneName;
    }

    public MotechEvent toMotechEvent() {
        Map<String, Object> param = new HashMap<>();
        param.put(EventDataKeys.EXTERNAL_ID, externalId);
        param.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        param.put(EventDataKeys.MILESTONE_NAME, startingMilestoneName);
        param.put(EventDataKeys.PREFERRED_ALERT_TIME, preferredAlertTime);
        param.put(EventDataKeys.REFERENCE_DATE, referenceDate);
        param.put(EventDataKeys.REFERENCE_TIME, referenceTime);
        param.put(EventDataKeys.ENROLLMENT_DATE, enrollmentDate);
        param.put(EventDataKeys.ENROLLMENT_TIME, enrollmentTime);
        return new MotechEvent(EventSubjects.USER_ENROLLED, param);
    }
}
