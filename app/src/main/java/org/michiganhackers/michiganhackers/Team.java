package org.michiganhackers.michiganhackers;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

public class Team {
    private String name;
    private String info;
    private TreeMap<String, Member> members;

    public Team(String name) {

        this.name = name;
        members = new TreeMap<>();
    }

    public void setMember(String memberName, Member member) {
        members.put(memberName, member);
    }
    public Member getMember(String memberName){
        return members.get(memberName);
    }
    public ArrayList<String> getMemberNames(){
        return members.keySet().toArray();
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
}
