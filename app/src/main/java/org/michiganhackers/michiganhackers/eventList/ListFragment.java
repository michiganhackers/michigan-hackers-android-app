package org.michiganhackers.michiganhackers.eventList;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import org.michiganhackers.michiganhackers.R;

import java.util.ArrayList;
import java.util.Arrays;

import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.SCOPES;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
// Todo: Implement google API in here?
// Todo: Should UI elements not be set until onActivityCreated to make sure MainActivity onCreate is done?
public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String STATE_EVENTS = "state_events";
    private static final String STATE_EVENT = "state_event";
    private ListRecyclerViewAdapter listRecyclerViewAdapter;
    private ArrayList<CalendarEvent> calendarEvents;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            calendarEvents = savedInstanceState.getParcelableArrayList(STATE_EVENTS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view_list);
        // Improves recyclerView performance
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Initialize adapter
        if(calendarEvents == null){
            calendarEvents = new ArrayList<>();
        }
        listRecyclerViewAdapter = new ListRecyclerViewAdapter(getActivity(), calendarEvents);
        recyclerView.setAdapter(listRecyclerViewAdapter);
        listRecyclerViewAdapter.setOnItemClickListener(new ListRecyclerViewAdapter.onItemClickListener(){
            @Override
            public void onItemClick(int position, View imageView) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra(STATE_EVENT,calendarEvents.get(position));
                if (Build.VERSION.SDK_INT >= 16){
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(requireActivity(),
                                    imageView,
                                    ViewCompat.getTransitionName(imageView));
                    requireActivity().startActivity(intent, options.toBundle());
                }
                else{
                    requireActivity().startActivity(intent);
                }
            }
        });
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        return layout;
    }
    public void updateListFragmentData(Bundle bundle){
        ArrayList<CalendarEvent> calendarEventArrayList = bundle.getParcelableArrayList(STATE_EVENTS);
        listRecyclerViewAdapter.updateData(calendarEventArrayList);
    }

    // Todo: Bundles should only hold a small amount of data. Change to viewmodel
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_EVENTS, calendarEvents);
    }

    @Override
    public void onRefresh() {
        CalendarAPI calAPI = new CalendarAPI(requireContext(),requireActivity(), this);
        calAPI.mCredential = GoogleAccountCredential.usingOAuth2(
                requireContext().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        calAPI.getResultsFromApi();

    }

    public SwipeRefreshLayout getmSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
