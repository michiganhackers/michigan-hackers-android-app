package org.michiganhackers.michiganhackers;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
// Todo: Implement google API in here?
// Todo: Save calendar info if fragment is stopped so it can restore later
// Todo: Should UI elements not be set until onActivityCreated to make sure MainActivity onCreate is done?
public class ListFragment extends Fragment{

    private static final String STATE_EVENTS = "state_events";
    private static final String STATE_EVENT = "state_event";
    private ListRecyclerViewAdapter listRecyclerViewAdapter;
    private ArrayList<CalendarEvent> calendarEvents;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.list_recycler);
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
                            makeSceneTransitionAnimation(getActivity(),
                                    imageView,
                                    ViewCompat.getTransitionName(imageView));
                    getActivity().startActivity(intent, options.toBundle());
                }
                else{
                    getActivity().startActivity(intent);
                }
            }
        });

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
}
