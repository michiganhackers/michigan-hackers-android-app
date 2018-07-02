package org.michiganhackers.michiganhackers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
/*
Implementation of Event class from google API. Needed to do this so that it could be made parcelable.
Refer to the following: https://developers.google.com/calendar/v3/reference/events
 */
// Todo: Implement parcel https://www.youtube.com/watch?v=qIhwPaa6rlU
// Todo: Change occurrences of List<Event> and such with List<CalendarEvent> in other files
// Todo: Implement static function to turn List<Event> to List<CalendarEvent>
public class CalendarEvent {
    private String summary;
    private String description;
    private String location;
    private boolean endTimeUnspecified;
    private String status;
    private Start start;
    private End end;
    private OriginalStartTime originalStartTime;

    public CalendarEvent(Event event) {
        this.summary = event.getSummary();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.endTimeUnspecified = event.getEndTimeUnspecified();
        this.status = event.getStatus();
        this.start = new Start(event.getStart().getDate(), event.getStart().getDateTime());
        this.end = new End(event.getEnd().getDate(), event.getEnd().getDateTime());
        this.originalStartTime = new OriginalStartTime(event.getOriginalStartTime().getDate(), event.getOriginalStartTime().getDateTime());
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public boolean isEndTimeUnspecified() {
        return endTimeUnspecified;
    }

    public String getStatus() {
        return status;
    }

    public Start getStart() {
        return start;
    }

    public End getEnd() {
        return end;
    }

    public OriginalStartTime getOriginalStartTime() {
        return originalStartTime;
    }

    public class Start{
        private DateTime date;
        private DateTime dateTime;

        public Start(DateTime date, DateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
        }

        public DateTime getDate() {
            return date;
        }

        public DateTime getDateTime() {
            return dateTime;
        }
    }
    public class End{
        private DateTime date;
        private DateTime dateTime;

        public End(DateTime date, DateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
        }

        public DateTime getDate() {
            return date;
        }

        public DateTime getDateTime() {
            return dateTime;
        }
    }
    public class OriginalStartTime{
        private DateTime date;
        private DateTime dateTime;

        public OriginalStartTime(DateTime date, DateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
        }

        public DateTime getDate() {
            return date;
        }

        public DateTime getDateTime() {
            return dateTime;
        }
    }
}
