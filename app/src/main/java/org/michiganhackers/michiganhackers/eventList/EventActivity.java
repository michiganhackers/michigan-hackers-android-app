package org.michiganhackers.michiganhackers.eventList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import org.michiganhackers.michiganhackers.R;


public class EventActivity extends AppCompatActivity {

    private static final String STATE_EVENT = "state_event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        CalendarEvent calendarEvent = getIntent().getExtras().getParcelable(STATE_EVENT);

        TextView tvEventName = findViewById(R.id.tv_event_name);
        TextView tvEventDate = findViewById(R.id.tv_event_date);
        TextView tvEventTime = findViewById(R.id.tv_event_time);
        TextView tvEventLocation = findViewById(R.id.tv_event_location);
        TextView tvEventDescription = findViewById(R.id.tv_event_description);

        tvEventName.setText(calendarEvent.getSummary());
        // Set start time
        DateTime startDateTime = calendarEvent.getStart().getDateTime();
        if (startDateTime != null) {
            tvEventDate.setText(TimeReformat.getDate(startDateTime));
            tvEventTime.setText(TimeReformat.getTime(startDateTime));
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            tvEventDate.setText(TimeReformat.getDate(calendarEvent.getStart().getDate()));
        }
        // Set end time
        //Todo: Change format for multi-day events
        DateTime endDateTime = calendarEvent.getEnd().getDateTime();
        if (endDateTime != null && startDateTime != null) {
            if(!TimeReformat.getDate(endDateTime).equals(TimeReformat.getDate(startDateTime))){
                tvEventDate.append(" - " + TimeReformat.getDate(endDateTime));
            }
            tvEventTime.append(" - " + TimeReformat.getTime(endDateTime));
        }
        // All-day events don't have start times, so just use
        // the start date.
        else{
            if(!TimeReformat.getDate(calendarEvent.getEnd().getDate()).equals(TimeReformat.getDate(calendarEvent.getStart().getDate()))){
                tvEventDate.append(" - " + TimeReformat.getDate(calendarEvent.getEnd().getDate()));
            }
        }

        tvEventDescription.setText(calendarEvent.getDescription());
        tvEventLocation.setText(getString(R.string.location, calendarEvent.getLocation()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }
}
