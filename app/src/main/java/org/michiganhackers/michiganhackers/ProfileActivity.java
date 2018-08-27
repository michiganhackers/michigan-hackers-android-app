package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private final static int PICK_IMAGE = 1;
    private static final String TAG = ProfileActivity.class.getName();
    private Uri croppedImageFileUri;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    Boolean teamSpinnerSelectedSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState != null) {
            croppedImageFileUri = savedInstanceState.getParcelable("croppedImageFileUri");
            if (croppedImageFileUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageFileUri);
                    ImageView profilePic = findViewById(R.id.profile_pic);
                    profilePic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Error while converting profilePicCropped to bitmap", e);
                }
            }
            teamSpinnerSelectedSet = savedInstanceState.getBoolean("teamSpinnerSelectedSet");
        }
        final ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        final EditText nameEditText = findViewById(R.id.profile_name);

        final Spinner majorSpinner = findViewById(R.id.profile_major);
        ArrayAdapter<CharSequence> majorSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.majors_array, android.R.layout.simple_spinner_item);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter majorNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(majorSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_major_hint), this);
        majorSpinner.setAdapter(majorNothingSelectedSpinnerAdapter);

        final Spinner yearSpinner = findViewById(R.id.profile_year);
        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter yearNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(yearSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_year_hint), this);
        yearSpinner.setAdapter(yearNothingSelectedSpinnerAdapter);

        final Spinner teamSpinner = findViewById(R.id.profile_team);
        final List<CharSequence> teamSpinnerItems = new ArrayList<>();
        final ArrayAdapter<CharSequence> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamSpinnerItems);
        teamSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter teamNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(teamSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_team_hint), this);
        teamSpinner.setAdapter(teamNothingSelectedSpinnerAdapter);

        // Note: The team spinner will update in real time. Some changes to the team list will change which team the user has selected.
        final Observer<List<String>> teamNamesObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> teamNames) {
                if (teamNames != null) {
                    // Populate team spinner
                    teamSpinnerItems.clear();
                    teamSpinnerItems.addAll(teamNames);
                    teamSpinnerAdapter.notifyDataSetChanged();
                }
                // Display the user's previous team selection if it hasn't been yet
                if(!teamSpinnerSelectedSet){
                    FirebaseUser user = auth.getCurrentUser();
                    if(user != null) {
                        String uid = user.getUid();
                        Member member = profileViewModel.getMember().getValue();
                        if (member != null) {
                            if (teamNothingSelectedSpinnerAdapter.getPosition(member.getTeams().get(1)) != -1) {
                                teamSpinner.setSelection(teamNothingSelectedSpinnerAdapter.getPosition(member.getTeam()));
                                teamSpinnerSelectedSet = true;
                            }
                        }
                    }
                }
            }
        };
        profileViewModel.getTeamNames().observe(this, teamNamesObserver);



        final Spinner titleSpinner = findViewById(R.id.profile_title);
        ArrayAdapter<CharSequence> titleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.title_array, android.R.layout.simple_spinner_item);
        titleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter titleNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(titleSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_title_hint), this);
        titleSpinner.setAdapter(titleNothingSelectedSpinnerAdapter);

        final EditText bioEditText = findViewById(R.id.profile_bio);
        final ImageView profilePic = findViewById(R.id.profile_pic);

        final Observer<Map<String, Team>> profileInfoObserver = new Observer<Map<String, Team>>() {
            @Override
            public void onChanged(@Nullable final Map<String, Team> teamsByName) {
                // Fill in profile fields with user's current info.
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    Member member = directoryViewModel.getMember(uid);
                    if (member != null) {
                        nameEditText.setText(member.getName());
                        if (majorNothingSelectedSpinnerAdapter.getPosition(member.getMajor()) != -1) {
                            majorSpinner.setSelection(majorNothingSelectedSpinnerAdapter.getPosition(member.getMajor()));
                        }
                        if (yearNothingSelectedSpinnerAdapter.getPosition(member.getYear()) != -1) {
                            yearSpinner.setSelection(yearNothingSelectedSpinnerAdapter.getPosition(member.getYear()));
                        }
                        if (titleNothingSelectedSpinnerAdapter.getPosition(member.getTitle()) != -1) {
                            titleSpinner.setSelection(titleNothingSelectedSpinnerAdapter.getPosition(member.getTitle()));
                        }
                        bioEditText.setText(member.getBio());
                        GlideApp.with(ProfileActivity.this)
                                .load(member.getPhotoUrl())
                                .placeholder(R.drawable.ic_directory)
                                .centerCrop()
                                .into(profilePic);
                        // Note that the observer is removed after the team with the current user is found and his/her info is added
                        // This is one reason why the team spinner is not populated with this observer
                        directoryViewModel.getTeamsByName().removeObserver(this);
                    }
                }
            }
        };
        if (savedInstanceState == null) {
            directoryViewModel.getTeamsByName().observe(this, profileInfoObserver);
        }
        Button submitChangesButton = findViewById(R.id.profile_submitChangesButton);
        submitChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = teamSpinner.getSelectedItem().toString();
                String memberName = nameEditText.getText().toString();
                String major = majorSpinner.getSelectedItem().toString();
                String year = yearSpinner.getSelectedItem().toString();
                String title = titleSpinner.getSelectedItem().toString();
                String bio = bioEditText.getText().toString();

                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    Member member = new Member(memberName, uid, bio, teamName, year, major, title);
                    directoryViewModel.addMember(member, croppedImageFileUri);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton imageEditButton = findViewById(R.id.profile_imageEditButton);
        imageEditButton.setOnClickListener(new View.OnClickListener() {
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
                Log.e(TAG, "Error while creating profilePic temp file", e);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            croppedImageFileUri = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageFileUri);
                ImageView profilePic = findViewById(R.id.profile_pic);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error while converting profilePicCropped to bitmap", e);
            }
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
        outState.putBoolean("teamSpinnerSelectedSet", teamSpinnerSelectedSet);
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
}
