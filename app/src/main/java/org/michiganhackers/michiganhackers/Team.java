package org.michiganhackers.michiganhackers;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

public class Team {
    private String name;
    private String info;
    @Exclude
    private TreeMap<String, Member> members;

    public Team(){
    }

    public Team(String name) {
        this.name = name;
        members = new TreeMap<>();
    }
    public void setMember(String memberName, Member member) {
        members.put(memberName, member);
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

}
