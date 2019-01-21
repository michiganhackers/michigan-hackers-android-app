package org.michiganhackers.michiganhackers.eventList;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeReformat {
    private static final String TAG = "TimeReformat";

    // Private constructor to prevent instantiation
    private TimeReformat(){}

    public static String getDate(DateTime dateTime) {
        SimpleDateFormat newSDF = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        if (dateTime.isDateOnly()) {
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            try {
                date = oldSDF.parse(dateTime.toString());
            } catch (Exception ex) {
                Log.e(TAG, "Failed to parse oldSDF", ex);
            }
            return newSDF.format(date);
        } else {
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Date date = new Date();
            try {
                date = oldSDF.parse(dateTime.toString());
            } catch (Exception ex) {
                Log.e(TAG, "Failed to parse oldSDF", ex);
            }
            return newSDF.format(date);
        }
    }

    // Only valid if input has time
    public static String getTime(DateTime dateTime) {
        if (dateTime.isDateOnly()) {
            Log.e(TAG, "getTime called on DateTime with date only");
            return null;
        } else {
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            SimpleDateFormat newSDF = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
            Date date = new Date();
            try {
                date = oldSDF.parse(dateTime.toString());
            } catch (Exception ex) {
                Log.e(TAG, "Failed to parse oldSDF", ex);
            }
            return newSDF.format(date);
        }
    }

    public static String getDateTime(CalendarEvent calendarEvent) {
        String eventDate = "";
        String eventTime = "";
        // Set start time
        DateTime startDateTime = calendarEvent.getStart().getDateTime();
        if (startDateTime != null) {
            eventDate += getDate(startDateTime);
            eventTime += getTime(startDateTime);
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            eventDate += getDate(calendarEvent.getStart().getDate());
        }
        // Set end time
        //Todo: Change format for multi-day events
        DateTime endDateTime = calendarEvent.getEnd().getDateTime();
        if (endDateTime != null && startDateTime != null) {
            if(!getDate(endDateTime).equals(getDate(startDateTime))){
                eventDate += " - " + getDate(endDateTime);
            }
            eventTime += " - " + getTime(endDateTime);
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            if(!getDate(calendarEvent.getEnd().getDate()).equals(getDate(calendarEvent.getStart().getDate()))){
                eventDate += " - " + getDate(calendarEvent.getEnd().getDate());
            }
        }

        return eventDate + '\n' + eventTime;
    }

}