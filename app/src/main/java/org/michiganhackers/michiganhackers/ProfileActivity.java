package org.michiganhackers.michiganhackers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.TreeMap;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final EditText nameEditText = findViewById(R.id.profile_name);
        final EditText majorEditText = findViewById(R.id.profile_major);
        final EditText yearEditText = findViewById(R.id.profile_year);
        final EditText teamEditText = findViewById(R.id.profile_team);
        EditText titleEditText = findViewById(R.id.profile_title);
        EditText bioEditText = findViewById(R.id.profile_bio);
        Button button = findViewById(R.id.profile_submitChangesButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeMap<String, Team> teamsByName = new TreeMap<>();
                UserDataRepo userDataRepo = new UserDataRepo(teamsByName);
                String teamName = nameEditText.getText().toString();
                if(!teamsByName.containsKey(teamName)) {
                    DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
                    Team team = new Team(teamName, teamsRef.push());
                    team.getKey().setValue(team);
                }
                DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child(teamName).child("Members");
                String memberName = nameEditText.getText().toString();
                String major = majorEditText.getText().toString();
                String year = yearEditText.getText().toString();
                String title = nameEditText.getText().toString();
                String bio = nameEditText.getText().toString();
                Member member = new Member(memberName, bio, teamName, year, major, title);
                if(!teamsByName.get(teamName).getMembers().containsKey(memberName)){
                    member.setKey(membersRef.push());
                }
                member.getKey().setValue(member);
            }
        });

    }
}
