package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_profile, container, false);

        final DirectoryViewModel directoryViewModel = ViewModelProviders.of(getActivity()).get(DirectoryViewModel.class);

        final EditText nameEditText = layout.findViewById(R.id.profile_name);
        final EditText majorEditText = layout.findViewById(R.id.profile_major);
        final EditText yearEditText = layout.findViewById(R.id.profile_year);
        final EditText teamEditText = layout.findViewById(R.id.profile_team);
        final EditText titleEditText = layout.findViewById(R.id.profile_title);
        final EditText bioEditText = layout.findViewById(R.id.profile_bio);
        Button button = layout.findViewById(R.id.profile_submitChangesButton);
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

        return layout;
    }
}
