package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
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
import android.widget.MultiAutoCompleteTextView;
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

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Boolean teamsSelectedSet = false;
    private ProfileViewModel profileViewModel;

    private EditText nameEditText;
    private Spinner yearSpinner;
    private MultiAutoCompleteTextView teamsMultiAutoCompleteTextView;
    MultiAutoCompleteTextView majorsMultiAutoCompleteTextView;
    private Spinner titleSpinner;
    private EditText bioEditText;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            teamsSelectedSet = savedInstanceState.getBoolean("teamsSelectedSet");
        }


        nameEditText = findViewById(R.id.profile_name);

        majorsMultiAutoCompleteTextView = findViewById(R.id.profile_majors);
        final ArrayAdapter<CharSequence> majorsMultiAutoCompleteTextViewAdapter = ArrayAdapter.createFromResource(this, R.array.majors_array, android.R.layout.simple_dropdown_item_1line);
        majorsMultiAutoCompleteTextView.setAdapter(majorsMultiAutoCompleteTextViewAdapter);
        majorsMultiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        yearSpinner = findViewById(R.id.profile_year);
        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter yearNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(yearSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_year_hint), this);
        yearSpinner.setAdapter(yearNothingSelectedSpinnerAdapter);

        teamsMultiAutoCompleteTextView = findViewById(R.id.profile_teams);
        List<CharSequence> teamsItems = new ArrayList<>();
        final ArrayAdapter<CharSequence> teamsMultiAutoCompleteTextViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teamsItems);
        teamsMultiAutoCompleteTextView.setAdapter(teamsMultiAutoCompleteTextViewAdapter);
        teamsMultiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

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

        titleSpinner = findViewById(R.id.profile_title);
        ArrayAdapter<CharSequence> titleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.title_array, android.R.layout.simple_spinner_item);
        titleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final NothingSelectedSpinnerAdapter titleNothingSelectedSpinnerAdapter = new NothingSelectedSpinnerAdapter(titleSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_title_hint), this);
        titleSpinner.setAdapter(titleNothingSelectedSpinnerAdapter);

        bioEditText = findViewById(R.id.profile_bio);
        profilePic = findViewById(R.id.profile_pic);

        final Observer<Member> memberObserver = new Observer<Member>() {
            @Override
            public void onChanged(@Nullable final Member member) {
                // Fill in profile fields with user's current info.
                if (member != null) {
                    nameEditText.setText(member.getName());
                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection(teamsMultiAutoCompleteTextViewAdapter);
                    }
                    for (String major : member.getMajors()) {
                        if (majorsMultiAutoCompleteTextViewAdapter.getPosition(major) != -1) {
                            String newText = majorsMultiAutoCompleteTextView.getText().toString() + major + ", ";
                            majorsMultiAutoCompleteTextView.setText(newText);
                        }
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
                    profileViewModel.getMember().removeObserver(this);
                }
            }

        };
        if (savedInstanceState == null) {
            profileViewModel.getMember().observe(this, memberObserver);
        }
        Button submitChangesButton = findViewById(R.id.profile_submitChangesButton);
        submitChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> teamNames = Arrays.asList(teamsMultiAutoCompleteTextView.getText().toString().split(", "));
                String memberName = nameEditText.getText().toString();
                List<String> majors = Arrays.asList(majorsMultiAutoCompleteTextView.getText().toString().split(", "));
                String year = yearSpinner.getSelectedItem().toString();
                String title = titleSpinner.getSelectedItem().toString();
                String bio = bioEditText.getText().toString();

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
                        String newText = teamsMultiAutoCompleteTextView.getText().toString() + team + ", ";
                        teamsMultiAutoCompleteTextView.setText(newText);
                    } else {
                        teamsSelectedSet = false;
                    }
                }
            }

        }
    }
}
