package org.michiganhackers.michiganhackers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
// Todo: Implement google API in here?
// Todo: Save calendar info if fragment is stopped so it can restore later
public class ListFragment extends Fragment{

    private static final String STATE_EVENTS = "state_events";
    private RecyclerView recyclerView;
    private ListRecyclerViewAdapter listRecyclerViewAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("debug","ListFragment onCreateView");
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = layout.findViewById(R.id.list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Improves recyclerView performance
        //recyclerView.setHasFixedSize(true);
        // Initialize adapter
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
        if(this.getArguments() != null){
            Log.d("debug","ListFragment savedInstanceState");
            calendarEvents = this.getArguments().getParcelableArrayList(STATE_EVENTS);
        }
        else{
            Log.d("debug","ListFragment no savedInstanceState");
        }
        listRecyclerViewAdapter = new ListRecyclerViewAdapter(getActivity(), calendarEvents);
        recyclerView.setAdapter(listRecyclerViewAdapter);

        return layout;
    }
    public void updateListFragmentData(Bundle bundle){
        ArrayList<CalendarEvent> events = bundle.getParcelableArrayList(STATE_EVENTS);
        listRecyclerViewAdapter.updateData(events);
    }
}
