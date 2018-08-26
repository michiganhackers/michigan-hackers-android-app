package org.michiganhackers.michiganhackers;

import java.util.Map;

public class AccountViewModel {
}


    public void removeMember(String uid) {
        for (Map.Entry<String, Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if (member != null) {
                DatabaseReference memberRef = teamsRef.child(member.getTeam()).child("members").child(uid);
                memberRef.removeValue();
            }
        }
    }