package org.michiganhackers.michiganhackers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import org.w3c.dom.Text;

public class EventActivity extends AppCompatActivity {

    private static final String STATE_EVENT = "state_event";
    private static final String TAG = EventActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        CalendarEvent calendarEvent = getIntent().getExtras().getParcelable(STATE_EVENT);

        TextView eventName = findViewById(R.id.event_name);
        TextView eventDate = findViewById(R.id.event_date);
        TextView eventTime = findViewById(R.id.event_time);
        TextView eventLocation = findViewById(R.id.event_location);
        TextView eventDescription = findViewById(R.id.event_description);

        eventName.setText(calendarEvent.getSummary());
        // Set start time
        DateTime startDateTime = calendarEvent.getStart().getDateTime();
        if (startDateTime != null) {
            eventDate.setText(TimeReformat.getDate(startDateTime));
            eventTime.setText(TimeReformat.getTime(startDateTime));
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            eventDate.setText(TimeReformat.getDate(calendarEvent.getStart().getDate()));
        }
        // Set end time
        //Todo: Change format for multi-day events
        DateTime endDateTime = calendarEvent.getEnd().getDateTime();
        if (endDateTime != null) {
            if(!TimeReformat.getDate(endDateTime).equals(TimeReformat.getDate(startDateTime))){
                eventDate.append(" - " + TimeReformat.getDate(endDateTime));
            }
            eventTime.append(" - " + TimeReformat.getTime(endDateTime));
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            if(!TimeReformat.getDate(calendarEvent.getEnd().getDate()).equals(TimeReformat.getDate(calendarEvent.getStart().getDate()))){
                eventDate.append(" - " + TimeReformat.getDate(calendarEvent.getEnd().getDate()));
            }
        }

        eventDescription.setText(calendarEvent.getDescription());
        eventLocation.setText("Location: " + calendarEvent.getLocation());
    }

}
