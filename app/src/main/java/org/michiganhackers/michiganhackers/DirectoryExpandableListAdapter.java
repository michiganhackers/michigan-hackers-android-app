package org.michiganhackers.michiganhackers;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

public class DirectoryExpandableListAdapter extends BaseExpandableListAdapter {
    private TreeMap<String, Team> teamsByName;
    private LayoutInflater inflater;

    public DirectoryExpandableListAdapter(Context context, TreeMap<String, Team> teamsByName) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.teamsByName = teamsByName;
    }

    @Override
    public Member getChild(int groupPosition, int childPosition){
        return getGroup(groupPosition).getMemberByIndex(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).getName();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.directory_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.item_name);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getSize();
    }

    @Override
    public Team getGroup(int groupPosition) {
        ArrayList<String> teamNames = new ArrayList<>(teamsByName.keySet());
        return teamsByName.get(teamNames.get(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return teamsByName.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).getName();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.directory_group, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.group_name);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // Todo: Is this necessary? Same teamsByName reference? Just notify dataset changed?
    public void updateData (TreeMap<String, Team> teamsByName_in) {
        if (teamsByName_in != null && teamsByName_in.size() > 0) {
            this.teamsByName = teamsByName_in;
            notifyDataSetChanged();
        }
    }
}

