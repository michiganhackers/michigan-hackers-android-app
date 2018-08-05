package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {
    public ProfileActivity() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final DirectoryViewModel directoryViewModel = ViewModelProviders.of(this).get(DirectoryViewModel.class);

        final EditText nameEditText = findViewById(R.id.profile_name);
        final EditText majorEditText = findViewById(R.id.profile_major);
        final EditText yearEditText = findViewById(R.id.profile_year);
        final EditText teamEditText = findViewById(R.id.profile_team);
        final EditText titleEditText = findViewById(R.id.profile_title);
        final EditText bioEditText = findViewById(R.id.profile_bio);
        Button button = findViewById(R.id.profile_submitChangesButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = teamEditText.getText().toString();
                String memberName = nameEditText.getText().toString();
                String major = majorEditText.getText().toString();
                String year = yearEditText.getText().toString();
                String title = titleEditText.getText().toString();
                String bio = bioEditText.getText().toString();
                Member member = new Member(memberName, bio, teamName, year, major, title);
                directoryViewModel.addMember(member);
            }
        });

    }
}
