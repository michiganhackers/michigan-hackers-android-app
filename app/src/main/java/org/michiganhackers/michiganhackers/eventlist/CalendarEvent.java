package org.michiganhackers.michiganhackers.eventlist;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

/*
Implementation of Event class from google API. Needed to do this so that it could be made parcelable.
Refer to the following if you want to implement more data from Event: https://developers.google.com/calendar/v3/reference/events
 */
public class CalendarEvent implements Parcelable{
    private final String summary;
    private final String description;
    private final String location;
    private final Boolean endTimeUnspecified;
    private final String status;
    private final Start start;
    private final End end;
    private final OriginalStartTime originalStartTime;

    private CalendarEvent(Event event) {
        this.summary = event.getSummary();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.endTimeUnspecified = event.getEndTimeUnspecified();
        this.status = event.getStatus();
        if(event.getStart() != null){
            this.start = new Start(event.getStart().getDate(), event.getStart().getDateTime());
        }
        else{
            this.start = null;
        }
        if(event.getEnd() != null){
            this.end = new End(event.getEnd().getDate(), event.getEnd().getDateTime());
        }
        else{
            this.end = null;
        }
        if(event.getOriginalStartTime() != null){
            this.originalStartTime = new OriginalStartTime(event.getOriginalStartTime().getDate(), event.getOriginalStartTime().getDateTime());
        }
        else{
            this.originalStartTime = null;
        }
    }
    // create a list of CalendarEvent from a list of Event
    public static ArrayList<CalendarEvent> createCalendarEventList(List<Event> events){
        ArrayList<CalendarEvent> calendarEventList = new ArrayList<>();
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

    public Start getStart() {
        return start;
    }

    public End getEnd() {
        return end;
    }

    public static class Start implements Parcelable {
        private final DateTime date;
        private final DateTime dateTime;

        Start(DateTime date, DateTime dateTime) {
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

        Start(Parcel in) {
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
        private final DateTime date;
        private final DateTime dateTime;

        End(DateTime date, DateTime dateTime) {
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

        End(Parcel in) {
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
        private final DateTime date;
        private final DateTime dateTime;

        OriginalStartTime(DateTime date, DateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
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

        OriginalStartTime(Parcel in) {
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
        dest.writeValue(this.endTimeUnspecified);
        dest.writeString(this.status);
        dest.writeParcelable(this.start, flags);
        dest.writeParcelable(this.end, flags);
        dest.writeParcelable(this.originalStartTime, flags);
    }

    private CalendarEvent(Parcel in) {
        this.summary = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.endTimeUnspecified = (Boolean) in.readValue(Boolean.class.getClassLoader());
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
