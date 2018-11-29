package org.michiganhackers.michiganhackers.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import org.michiganhackers.michiganhackers.GlideApp;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.ThemeHandler;
import org.michiganhackers.michiganhackers.login.LoginActivity;
import org.michiganhackers.michiganhackers.directory.Member;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private final static int PICK_IMAGE = 1;
    private static final String TAG = ProfileActivity.class.getName();
    private Uri croppedImageFileUri;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Boolean teamsSelectedSet = false;
    private ProfileViewModel profileViewModel;

    private EditText etProfileName, etBio;
    private Spinner spinnerYear, spinnerTitle;
    private MultiAutoCompleteTextView autoCompleteTvTeams;
    MultiAutoCompleteTextView autoCompleteTvMajors;
    private ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHandler themeHan = new ThemeHandler(this);
        themeHan.setTheme();
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(uid);
            profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);
        } else {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        imgProfilePic = findViewById(R.id.image_profile_pic);
        if (savedInstanceState != null) {
            croppedImageFileUri = savedInstanceState.getParcelable("croppedImageFileUri");
            if(croppedImageFileUri != null){
                GlideApp.with(ProfileActivity.this)
                        .load(croppedImageFileUri)
                        .placeholder(R.drawable.ic_directory)
                        .centerCrop()
                        .into(imgProfilePic);
            }else{
                Member member = profileViewModel.getMember().getValue();
                if (member != null) {
                    GlideApp.with(ProfileActivity.this)
                            .load(member.getPhotoUrl())
                            .placeholder(R.drawable.ic_directory)
                            .centerCrop()
                            .into(imgProfilePic);
                }
            }
            teamsSelectedSet = savedInstanceState.getBoolean("teamsSelectedSet");
        }


        etProfileName = findViewById(R.id.et_profile_name);

        autoCompleteTvMajors = findViewById(R.id.tv_majors);
        final List<CharSequence> majorsItems = new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(R.array.majors_array)));
        final ArrayAdapter<CharSequence> majorsMultiAutoCompleteTextViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, majorsItems);
        autoCompleteTvMajors.setAdapter(majorsMultiAutoCompleteTextViewAdapter);
        autoCompleteTvMajors.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        spinnerYear = findViewById(R.id.spinner_year);
        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter yearNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(yearSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_year_hint), this);
        spinnerYear.setAdapter(yearNothingSelectedSpinnerAdapter);

        autoCompleteTvTeams = findViewById(R.id.tv_teams);
        final List<CharSequence> teamsItems = new ArrayList<>();
        final ArrayAdapter<CharSequence> teamsMultiAutoCompleteTextViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teamsItems);
        autoCompleteTvTeams.setAdapter(teamsMultiAutoCompleteTextViewAdapter);
        autoCompleteTvTeams.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        final Observer<List<String>> teamNamesObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> teamNames) {
                if (teamNames != null) {
                    // Populate team spinner
                    teamsMultiAutoCompleteTextViewAdapter.clear();
                    teamsMultiAutoCompleteTextViewAdapter.addAll(teamNames);
                    teamsMultiAutoCompleteTextViewAdapter.notifyDataSetChanged();

                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection(teamsMultiAutoCompleteTextViewAdapter);
                    }
                }
            }
        };
        profileViewModel.getTeamNames().observe(this, teamNamesObserver);

        spinnerTitle = findViewById(R.id.spinner_title);
        ArrayAdapter<CharSequence> titleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.title_array, android.R.layout.simple_spinner_item);
        titleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter titleNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(titleSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_title_hint), this);
        spinnerTitle.setAdapter(titleNothingSelectedSpinnerAdapter);

        etBio = findViewById(R.id.et_bio);

        final Observer<Member> memberObserver = new Observer<Member>() {
            @Override
            public void onChanged(@Nullable final Member member) {
                // Fill in profile fields with user's current info.
                if (member != null) {
                    GlideApp.with(ProfileActivity.this)
                            .load(member.getPhotoUrl())
                            .placeholder(R.drawable.ic_directory)
                            .centerCrop()
                            .into(imgProfilePic);
                    etProfileName.setText(member.getName());
                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection(teamsMultiAutoCompleteTextViewAdapter);
                    }
                    for (String major : member.getMajors()) {
                        if (majorsMultiAutoCompleteTextViewAdapter.getPosition(major) != -1) {
                            String newText = autoCompleteTvMajors.getText().toString() + major + ", ";
                            autoCompleteTvMajors.setText(newText);
                        }
                    }
                    if (yearNothingSelectedSpinnerAdapter.getPosition(member.getYear()) != -1) {
                        spinnerYear.setSelection(yearNothingSelectedSpinnerAdapter.getPosition(member.getYear()));
                    }
                    if (titleNothingSelectedSpinnerAdapter.getPosition(member.getTitle()) != -1) {
                        spinnerTitle.setSelection(titleNothingSelectedSpinnerAdapter.getPosition(member.getTitle()));
                    }
                    etBio.setText(member.getBio());
                    // Note that the observer is removed after the team with the current user is found and his/her info is added
                    // This is one reason why the team spinner is not populated with this observer
                    profileViewModel.getMember().removeObserver(this);
                }
            }

        };
        if (savedInstanceState == null) {
            profileViewModel.getMember().observe(this, memberObserver);
        }
        Button btnSubmitChanges = findViewById(R.id.btn_submit_changes);
        btnSubmitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> teamNames = new ArrayList<>(Arrays.asList(autoCompleteTvTeams.getText().toString().trim().split("\\s*,\\s*")));
                // remove duplicates
                Set<String> teamNamesSet = new HashSet<>(teamNames);
                teamNames.clear();
                teamNames.addAll(teamNamesSet);
                String memberName = etProfileName.getText().toString().trim();
                List<String> majors = new ArrayList<>(Arrays.asList(autoCompleteTvMajors.getText().toString().trim().split("\\s*,\\s*")));
                // remove duplicates
                Set<String> majorsSet = new HashSet<>(majors);
                majors.clear();
                majors.addAll(majorsSet);
                String year = spinnerYear.getSelectedItem().toString();
                String title = spinnerTitle.getSelectedItem().toString();
                String bio = etBio.getText().toString().trim();

                // Check user input and show warnings accordingly
                // Todo: Make errors for spinners?
                Boolean warningShown = false;
                if (teamNames.size() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please complete teams field", Toast.LENGTH_LONG).show();
                    warningShown = true;
                } else if (majors.size() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please complete majors field", Toast.LENGTH_LONG).show();
                    warningShown = true;
                } else if (year.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please complete majors field", Toast.LENGTH_LONG).show();
                    warningShown = true;
                } else if (title.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please complete title field", Toast.LENGTH_LONG).show();
                    warningShown = true;
                }
                if (memberName.equals("")) {
                    etProfileName.setError("Enter name");
                    warningShown = true;
                }
                for (String majorName : majors) {
                    if (!majorsItems.contains(majorName)) {
                        // Todo: automatically delete incorrect teams?
                        autoCompleteTvMajors.setError("All majors must exist");
                        warningShown = true;
                        break;
                    }
                }
                for (String teamName : teamNames) {
                    if (!teamsItems.contains(teamName)) {
                        // Todo: automatically delete incorrect teams?
                        autoCompleteTvTeams.setError("All teams must exist");
                        warningShown = true;
                        break;
                    }
                }
                if (bio.equals("")) {
                    etBio.setError("Enter bio");
                    warningShown = true;
                }

                if (!warningShown) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        Member member = new Member(memberName, uid, bio, teamNames, year, majors, title);
                        // Todo: Add listener to setMember to add progressBar as well as toast if failed to update profile
                        profileViewModel.setMember(member, croppedImageFileUri);
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        FloatingActionButton btnEditProfilePic = findViewById(R.id.btn_edit_profile_pic);
        btnEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri sourceImageFileUri = data.getData();
            try {
                // Create temporary file to store results of crop
                String imageFileName = "profilePicCropped.jpeg";
                File croppedImageFile = File.createTempFile(imageFileName, null, getCacheDir());
                Uri destinationImageFileUri = Uri.fromFile(croppedImageFile);
                UCrop.of(sourceImageFileUri, destinationImageFileUri).withAspectRatio(1, 1).start(ProfileActivity.this);
            } catch (IOException e) {
                Log.e(TAG, "Error while creating imgProfilePic temp file", e);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            croppedImageFileUri = UCrop.getOutput(data);
            GlideApp.with(ProfileActivity.this)
                    .load(croppedImageFileUri)
                    .placeholder(R.drawable.ic_directory)
                    .centerCrop()
                    .into(imgProfilePic);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(TAG, "Error cropping image", cropError);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (croppedImageFileUri != null) {
            outState.putParcelable("croppedImageFileUri", croppedImageFileUri);
        }
        outState.putBoolean("teamsSelectedSet", teamsSelectedSet);
        super.onSaveInstanceState(outState);
    }

    private class MutableInteger {
        private int value;

        public MutableInteger(int value) {
            this.value = value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    void setTeamsSelection(ArrayAdapter<CharSequence> teamsMultiAutoCompleteTextViewAdapter) {
        Member member = profileViewModel.getMember().getValue();
        if (member != null) {
            if (member.getTeams().size() != 0) {
                teamsSelectedSet = true;
                for (String team : member.getTeams()) {
                    if (teamsMultiAutoCompleteTextViewAdapter.getPosition(team) != -1) {
                        String newText = autoCompleteTvTeams.getText().toString() + team + ", ";
                        autoCompleteTvTeams.setText(newText);
                    } else {
                        teamsSelectedSet = false;
                    }
                }
            }

        }
    }
}
