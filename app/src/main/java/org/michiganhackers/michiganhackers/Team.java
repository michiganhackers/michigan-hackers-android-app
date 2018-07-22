package org.michiganhackers.michiganhackers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

public class Team {
    private String name;
    private String info;
    private Map<String, Member> members = new TreeMap<>();

    public Team(){
    }

    public Team(String name) {
        this.name = name;
    }

    public void setMember(Member member) {
        members.put(member.getName(), member);
    }
    public Member getMemberByIndex(int index){
        ArrayList<String> memberNames = new ArrayList<>(members.keySet());
        return members.get(memberNames.get(index));
    }
    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSize(){
        return members.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Member> getMembers() {
        return members;
    }
}
