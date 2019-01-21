package org.michiganhackers.michiganhackers.eventList;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
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
        TextView tvEventDateTime = findViewById(R.id.tv_event_date_time);
        TextView tvEventLocation = findViewById(R.id.tv_event_location);
        TextView tvEventDescription = findViewById(R.id.tv_event_description);

        String eventDateTime = TimeReformat.getDateTime(calendarEvent);
        String eventDescription = calendarEvent.getDescription();
        String eventLocation = calendarEvent.getLocation();

        tvEventName.setText(calendarEvent.getSummary());
        if(!eventDateTime.isEmpty()){
            tvEventDateTime.setText(eventDateTime);
        }
        else{
            tvEventDateTime.setVisibility(View.GONE);
        }

        if(eventDescription != null && !eventDescription.isEmpty()){
            tvEventDescription.setText(eventDescription);
        }
        else{
            tvEventDescription.setVisibility(View.GONE);
        }

        if(eventLocation != null && !eventLocation.isEmpty()){
            tvEventLocation.setText(eventLocation);
        }
        else{
            tvEventLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }
}
