package org.michiganhackers.michiganhackers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

/*
Implementation of Event class from google API. Needed to do this so that it could be made parcelable.
Refer to the following: https://developers.google.com/calendar/v3/reference/events
 */
public class CalendarEvent implements Parcelable{
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
    // create a list of CalendarEvent from a list of Event
    public static List<CalendarEvent> createCalendarEventList(List<Event> events){
        List<CalendarEvent> calendarEventList = new ArrayList<>();
        for(int i = 0; i<events.size(); ++i){
            calendarEventList.add(new CalendarEvent(events.get(i)));
        }
        return calendarEventList;
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

    public static class Start implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.date);
            dest.writeSerializable(this.dateTime);
        }

        protected Start(Parcel in) {
            this.date = (DateTime) in.readSerializable();
            this.dateTime = (DateTime) in.readSerializable();
        }

        public static final Creator<Start> CREATOR = new Creator<Start>() {
            @Override
            public Start createFromParcel(Parcel source) {
                return new Start(source);
            }

            @Override
            public Start[] newArray(int size) {
                return new Start[size];
            }
        };
    }
    public static class End implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.date);
            dest.writeSerializable(this.dateTime);
        }

        protected End(Parcel in) {
            this.date = (DateTime) in.readSerializable();
            this.dateTime = (DateTime) in.readSerializable();
        }

        public static final Creator<End> CREATOR = new Creator<End>() {
            @Override
            public End createFromParcel(Parcel source) {
                return new End(source);
            }

            @Override
            public End[] newArray(int size) {
                return new End[size];
            }
        };
    }
    public static class OriginalStartTime implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.date);
            dest.writeSerializable(this.dateTime);
        }

        protected OriginalStartTime(Parcel in) {
            this.date = (DateTime) in.readSerializable();
            this.dateTime = (DateTime) in.readSerializable();
        }

        public static final Creator<OriginalStartTime> CREATOR = new Creator<OriginalStartTime>() {
            @Override
            public OriginalStartTime createFromParcel(Parcel source) {
                return new OriginalStartTime(source);
            }

            @Override
            public OriginalStartTime[] newArray(int size) {
                return new OriginalStartTime[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeByte(this.endTimeUnspecified ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
        dest.writeParcelable(this.start, flags);
        dest.writeParcelable(this.end, flags);
        dest.writeParcelable(this.originalStartTime, flags);
    }

    protected CalendarEvent(Parcel in) {
        this.summary = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.endTimeUnspecified = in.readByte() != 0;
        this.status = in.readString();
        this.start = in.readParcelable(Start.class.getClassLoader());
        this.end = in.readParcelable(End.class.getClassLoader());
        this.originalStartTime = in.readParcelable(OriginalStartTime.class.getClassLoader());
    }

    public static final Creator<CalendarEvent> CREATOR = new Creator<CalendarEvent>() {
        @Override
        public CalendarEvent createFromParcel(Parcel source) {
            return new CalendarEvent(source);
        }

        @Override
        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
}
