package org.michiganhackers.michiganhackers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import java.util.List;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private List<CalendarEvent> dataSet;
    public ListRecyclerViewAdapter(Context context, List<CalendarEvent> dataSet){
        inflater = LayoutInflater.from(context);
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventName.setText(dataSet.get(position).getSummary());
        DateTime start = dataSet.get(position).getStart().getDateTime();
        if (start == null) {
            // All-day events don't have start times, so just use
            // the start date.
            start = dataSet.get(position).getStart().getDate();
        }
        holder.eventTime.setText(start.toString());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // Used to update event data
    public void updateData (List<CalendarEvent> data) {
        if (data != null && data.size() > 0) {
            dataSet.clear();
            dataSet.addAll(data);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventName;
        TextView eventTime;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventTime = itemView.findViewById(R.id.event_time);
        }
    }
}
