package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DirectoryExpandableListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private Map<String, Team> teamsByName;
    private Context context;

    public DirectoryExpandableListAdapter(Context context) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.teamsByName = new TreeMap<>();
        this.context = context;
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

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.directory_item, null);
        }
        TextView memberName = convertView.findViewById(R.id.item_memberName);
        TextView memberTitle = convertView.findViewById(R.id.item_memberTitle);
        ImageView memberPhoto = convertView.findViewById(R.id.item_memberPhoto);

        memberName.setText(getChild(groupPosition, childPosition).getName());
        memberTitle.setText(getChild(groupPosition, childPosition).getTitle());
        GlideApp.with(context)
                .load(getChild(groupPosition, childPosition).getPhotoUrl())
                .placeholder(R.drawable.ic_directory)
                .into(memberPhoto);

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

    public void setTeamsByName(Map<String, Team> teamsByName) {
        this.teamsByName = teamsByName;
    }
}

