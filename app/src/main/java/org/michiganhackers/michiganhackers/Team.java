package org.michiganhackers.michiganhackers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

public class Team {
    private String name;
    private String info;
    private DatabaseReference key;
    @Exclude
    private TreeMap<String, Member> members;

    public Team(){
        members = new TreeMap<>();
    }

    public Team(String name) {
        this.name = name;
        members = new TreeMap<>();
    }

    public Team(String name, DatabaseReference key) {
        this.name = name;
        this.key = key;
        members = new TreeMap<>();
    }

    @Exclude
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

    @Exclude
    public int getSize(){
        return members.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public TreeMap<String, Member> getMembers() {
        return members;
    }

    public DatabaseReference getKey() {
        return key;
    }

    public void setKey(DatabaseReference key) {
        this.key = key;
    }
}
