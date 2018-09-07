package org.michiganhackers.michiganhackers.eventlist;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.MODE_PRIVATE;



public class CalenderAPI{

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    private static final String STATE_EVENTS = "state_events";
    private static final String TAG = "CalendarAPI";

    private final Context context;
    private final Activity activity;
    private final ListFragment listFragment;
    public GoogleAccountCredential mCredential;

    public CalenderAPI(Context context, Activity activity, ListFragment listFrag){
        this.context = context;
        this.activity = activity;
        this.listFragment = listFrag;
    }


    public void getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }
        else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
        else if (!isDeviceOnline()) {
            // Todo: mOutputText.setText("No network connection available.");
            Log.e(TAG,"No network connection available");
        }
        else {
            new MakeRequestTask(mCredential, this).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                context, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = activity.getPreferences(MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccount(new Account(accountName,"org.michiganhackers.michiganhackers"));
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
            getResultsFromApi();
        }
    }


    private boolean isDeviceOnline() {
        NetworkInfo networkInfo = null;
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    static class MakeRequestTask extends AsyncTask<Void, Void, ArrayList<CalendarEvent>> {
        private final com.google.api.services.calendar.Calendar mService;
        final CalenderAPI calAPI;

        MakeRequestTask(GoogleAccountCredential credential, CalenderAPI ca) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            this.calAPI = ca;
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Michigan Hackers")
                    .build();
        }

        @Override
        protected ArrayList<CalendarEvent> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                cancel(true);
                return null;
            }
        }

        private ArrayList<CalendarEvent> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list("8n8u58ssric1hmm84jvkvl9d68@group.calendar.google.com")
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            return CalendarEvent.createCalendarEventList(events.getItems());
        }

        @Override
        protected void onPreExecute() {
            // Todo: Set UI to something? Loading screen?
        }

        @Override
        protected void onPostExecute(ArrayList<CalendarEvent> output) {
            if (output == null || output.size() == 0) {
                Log.e(TAG, "No results returned");
            }

            calAPI.updateCalendar(output);
        }
        /*
        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    activity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.e(TAG,"The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.e(TAG,"Request cancelled");
            }
        }
        */
    }
    private void updateCalendar(ArrayList<CalendarEvent> output) {

        // Send bundle of calendar events
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_EVENTS, output);
        listFragment.updateListFragmentData(bundle);
        listFragment.getmSwipeRefreshLayout().setRefreshing(false);
    }
}