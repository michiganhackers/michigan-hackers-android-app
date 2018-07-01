import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventAttendee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

private com.google.api.services.calendar.Calendar mService=null;

private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private Exception mLastError = null;
    private boolean FLAG = false;

    public MakeRequestTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(“Google Calendar API Android Quickstart”)
                .build();
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void… params) {
        try {
            getDataFromApi();
        } catch (Exception e) {
            e.printStackTrace();
            mLastError = e;
            cancel(true);
            return null;
        }
        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     *
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private void getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list(“primary”)
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy(“startTime”)
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        ScheduledEvents scheduledEvents;
        scheduledEventsList.clear();
        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            scheduledEvents = new ScheduledEvents();
            scheduledEvents.setEventId(event.getId());
            scheduledEvents.setDescription(event.getDescription());
            scheduledEvents.setEventSummery(event.getSummary());
            scheduledEvents.setLocation(event.getLocation());
            scheduledEvents.setStartDate(start.toString());
            scheduledEvents.setEndDate(“”);
            StringBuffer stringBuffer = new StringBuffer();
            if (event.getAttendees() != null) {
                for (EventAttendee eventAttendee : event.getAttendees()) {
                    if (eventAttendee.getEmail() != null)
                        stringBuffer.append(eventAttendee.getEmail() + ”       “);
                }
                scheduledEvents.setAttendees(stringBuffer.toString());
            } else {
                scheduledEvents.setAttendees(“”);
            }
            scheduledEventsList.add(scheduledEvents);
            System.out.println(“—–“ + event.getDescription() +”, “+event.getId() +”, “
            +event.getLocation());
            System.out.println(event.getAttendees());
            eventStrings.add(
                    String.format(“ % s( % s)”,event.getSummary(), start));
        }
    }

    @Override
    protected void onPreExecute() {
        mOutputText.setText(“”);
        mProgress.show();
    }

    @Override
    protected void onPostExecute(List<String> output) {
        mProgress.hide();
        System.out.println(“——————–“ + scheduledEventsList.size());
        if (scheduledEventsList.size() <= 0) {
            mOutputText.setText(“No results returned.”);
        } else {
            eventListAdapter = new EventListAdapter(CalendarActivity.this, scheduledEventsList);
            eventListView.setAdapter(eventListAdapter);
        }
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        CalendarActivity.REQUEST_AUTHORIZATION);
            } else {
                mOutputText.setText(“The following error occurred:\n”
                +mLastError.getMessage());
            }
        } else {
            mOutputText.setText(“Request cancelled.”);
        }
    }
}