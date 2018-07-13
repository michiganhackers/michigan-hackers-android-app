package org.michiganhackers.michiganhackers;

import java.util.List;
import java.util.SortedSet;

public class Team {
    private String name;
    private String info;
    private SortedSet<Member> members;

    public Team(String name) {
        this.name = name;
    }

    public SortedSet<Member> getMembers() {
        return members;
    }

    public void setMembers(SortedSet<Member> members) {
        this.members = members;
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
