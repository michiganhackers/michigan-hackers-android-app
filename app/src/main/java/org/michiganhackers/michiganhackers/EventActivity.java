package org.michiganhackers.michiganhackers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

public class EventActivity extends AppCompatActivity {

    private CalendarEvent calendarEvent;
    private static final String STATE_EVENT = "state_event";
    private static final String TAG = EventActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        calendarEvent = getIntent().getExtras().getParcelable(STATE_EVENT);

        TextView eventName = findViewById(R.id.event_name);
        TextView eventTime = findViewById(R.id.event_time);
        TextView eventDescription = findViewById(R.id.event_description);

        eventName.setText(calendarEvent.getSummary());
        DateTime start = calendarEvent.getStart().getDateTime();
        if (start == null) {
            // All-day events don't have start times, so just use
            // the start date.
            start = calendarEvent.getStart().getDate();
        }
        eventTime.setText(start.toString());
        eventDescription.setText(calendarEvent.getDescription());
    }
}
