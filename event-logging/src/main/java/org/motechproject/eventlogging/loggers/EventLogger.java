package org.motechproject.eventlogging.loggers;

import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.matchers.LoggableEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a generic event logger. All loggers have a list of
 * loggable events that they should be able to provide logging functionality for.
 */
public abstract class EventLogger {

    private List<LoggableEvent> loggableEvents = new ArrayList<>();

    public void addLoggableEvents(List<LoggableEvent> loggableEvents) {
        this.loggableEvents.addAll(loggableEvents);
    }

    public void removeLoggableEvents(List<LoggableEvent> loggableEvents) {
        this.loggableEvents.removeAll(loggableEvents);
    }

    public void clearLoggableEvents() {
        loggableEvents.clear();
    }

    public List<LoggableEvent> getLoggableEvents() {
        return loggableEvents;
    }

    public abstract void log(MotechEvent eventToLog);

}
