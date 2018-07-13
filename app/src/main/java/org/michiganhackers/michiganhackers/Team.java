package org.michiganhackers.michiganhackers;

import java.util.List;

public class Team {
    private String name;
    private String info;
    private List<Member> members;

    public Team(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
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
