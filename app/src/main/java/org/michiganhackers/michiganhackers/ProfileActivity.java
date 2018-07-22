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
    private TreeMap<String, Team> teamsByName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserDataRepo userDataRepo = new UserDataRepo(teamsByName);

        Button button = findViewById(R.id.profile_submitChangesButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = findViewById(R.id.profile_name);
                EditText teamEditText = findViewById(R.id.profile_team);
                EditText titleEditText = findViewById(R.id.profile_title);
                EditText bioEditText = findViewById(R.id.profile_bio);

                

            }
        });

    }
}
