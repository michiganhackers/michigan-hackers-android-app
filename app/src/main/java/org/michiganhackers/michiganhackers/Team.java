package org.michiganhackers.michiganhackers;

import java.util.ArrayList;
import java.util.Map;
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
        members.put(member.getUid(), member);
    }
    public Member getMemberByIndex(int index){
        ArrayList<String> memberIds = new ArrayList<>(members.keySet());
        return members.get(memberIds.get(index));
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

    public Member getMember(String uid){
        if(members.containsKey(uid)){
            return members.get(uid);
        }
        else
        {
            return null;
        }
    }

}
