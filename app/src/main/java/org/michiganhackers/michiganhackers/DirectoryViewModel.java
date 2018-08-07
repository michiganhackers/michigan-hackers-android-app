package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;

public class DirectoryViewModel extends ViewModel{
    public static final String TAG = "DirectoryViewModel";
    private MutableLiveData<Map<String, Team>> teamsByName;
    private Map<String, Team> teamsByNameLocal;
    private DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
    public DirectoryViewModel(){
        teamsByNameLocal = new TreeMap<>();
        if(this.teamsByName != null){
            return;
        }
        else{
            teamsByName = new MutableLiveData<>();
            teamsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByNameLocal.put(team.getName(), team);
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByNameLocal.put(team.getName(), team);
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByNameLocal.remove(team.getName());
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Todo
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Todo
                }
            });
        }
    }
    LiveData<Map<String, Team>> getTeamsByName(){
        return teamsByName;
    }

    public void addMember(Member member){
        if(teamsByNameLocal.containsKey(member.getTeam())) {
            DatabaseReference memberRef = teamsRef.child(member.getTeam()).child("members").child(member.getUid());

            // If the member already exists and the name is different, change firebase auth display name
            if(teamsByNameLocal.get(member.getTeam()).getMember(member.getUid())!=null &&
                    !teamsByNameLocal.get(member.getTeam()).getMember(member.getUid()).getName().equals(member.getName())){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(member.getName()).build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e(TAG,"Failed to update auth display name");
                                    }
                                }
                            });
                }
            }

            memberRef.setValue(member);
        }
        else{
            Team team = new Team(member.getTeam());
            team.setMember(member);
            teamsRef.child(member.getTeam()).setValue(team);
        }
    }

    public Member getMember(String uid){
        for(Map.Entry<String,Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if(member != null){
                return member;
            }
        }
        return null;
    }

    public void removeMember(String uid){
        for(Map.Entry<String,Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if(member!=null){
                DatabaseReference memberRef = teamsRef.child(member.getTeam()).child("members").child(uid);
                memberRef.removeValue();
            }
        }
    }
}