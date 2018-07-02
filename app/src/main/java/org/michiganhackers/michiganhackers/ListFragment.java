package org.michiganhackers.michiganhackers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity.
// OR getActivity() == null

public class ListFragment extends Fragment{

    private RecyclerView recyclerView;
    private ListRecyclerViewAdapter listRecyclerViewAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = layout.findViewById(R.id.list_recycler);
        // Improves recyclerView performance
        recyclerView.setHasFixedSize(true);
        // Initialize adapter with empty list. Adapter will be updated in onPostExecute() in MainActivity
        listRecyclerViewAdapter = new ListRecyclerViewAdapter(getActivity(), new ArrayList<Event>());
        recyclerView.setAdapter(listRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //            listRecyclerViewAdapter.updateData(output);


        return layout;
    }
}
