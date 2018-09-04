package org.michiganhackers.michiganhackers.directory;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.michiganhackers.michiganhackers.GlideApp;
import org.michiganhackers.michiganhackers.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DirectoryExpandableListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private List<Team> teams;
    private List<Member> members;
    private Context context;

    DirectoryExpandableListAdapter(Context context) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.teams = new ArrayList<>();
        this.members = new ArrayList<>();
        this.context = context;
    }

    @Override
    public Member getChild(int groupPosition, int childPosition){
        Team team = teams.get(groupPosition);
        int pos = -1;
        for (int i = 0; i < members.size(); ++i){
            if(members.get(i).getTeams().contains(team.getName())){
                ++pos;
            }
            if(pos == childPosition){
                return members.get(i);
            }
        }
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.directory_item, parent, false);
        }
        TextView memberName = convertView.findViewById(R.id.item_memberName);
        TextView memberTitle = convertView.findViewById(R.id.item_memberTitle);
        ImageView memberPhoto = convertView.findViewById(R.id.item_memberPhoto);

        memberName.setText(getChild(groupPosition, childPosition).getName());
        memberTitle.setText(getChild(groupPosition, childPosition).getTitle());
        GlideApp.with(context)
                .load(getChild(groupPosition, childPosition).getPhotoUrl())
                .placeholder(R.drawable.ic_directory)
                .centerCrop()
                .into(memberPhoto);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Team team = teams.get(groupPosition);
        int ct = 0;
        for (int i = 0; i < members.size(); ++i){
            if(members.get(i).getTeams().contains(team.getName())){
                ++ct;
            }
        }
        return ct;
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
            convertView = inflater.inflate(R.layout.directory_group, parent, false);
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

    // Note: Parameter type is TreeMap and not Map to make sure it gets sorted teams
    public void setTeams(Map<String,Team> teams) {
        this.teams = new ArrayList<>(teams.values());
    }

    public void setMembers(Map<String,Member> members) {
        this.members = new ArrayList<>(members.values());
        Collections.sort(this.members,new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}

