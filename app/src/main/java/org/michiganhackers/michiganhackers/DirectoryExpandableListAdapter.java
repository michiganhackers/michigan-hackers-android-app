package org.michiganhackers.michiganhackers;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

public class DirectoryExpandableListAdapter extends BaseExpandableListAdapter {
    private SortedSet<Team> teams;
    private HashMap<String, SortedSet<Member>> membersByTeam;
    private LayoutInflater inflater;

    public DirectoryExpandableListAdapter(Context context, SortedSet<Team> teams, HashMap<String, SortedSet<Member>> membersByTeam) {
        this.inflater = LayoutInflater.from(context);
        this.teams = teams;
        this.membersByTeam = membersByTeam;
    }

    @Override
    public Member getChild(int groupPosition, int childPosition){
        return membersByTeam.get(teams.get(groupPosition).getName()).get(childPosition);
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
        return membersByTeam.get(teams.get(groupPosition).getName()).size();
    }

    @Override
    public Team getGroup(int groupPosition) {
        return teams.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return teams.size();
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
}

