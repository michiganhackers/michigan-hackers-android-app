package org.michiganhackers.michiganhackers.profile;

import androidx.lifecycle.Observer;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.GlideApp;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.SemicolonTokenizer;
import org.michiganhackers.michiganhackers.Util;
import org.michiganhackers.michiganhackers.directory.Member;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends FirebaseAuthActivity {
    private static final int PICK_IMAGE = 1;
    public static final String CROPPED_IMAGE_FILE_URI = "croppedImageFileUri";
    private final String TAG = getClass().getCanonicalName();
    private Uri croppedImageFileUri;

    private Boolean teamsSelectedSet = false;

    protected ProfileViewModel profileViewModel;

    private TextInputEditText etProfileName, etBio;
    private AutoCompleteTextView autoCompleteTvYear, autoCompleteTvTitle;
    private TextInputLayout textInputProfileName, textInputMajors, textInputTeams, textInputBio, textInputTitle, textInputYear;
    private ImageView imgProfilePic;
    private CoordinatorLayout coordinatorLayout;
    CustomMultiAutoCompleteTextView customAutoCompleteTeams, customAutoCompleteMajors;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (firebaseUser == null) {
            return;
        }

        String uid = firebaseUser.getUid();
        ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(uid);
        profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);

        imgProfilePic = findViewById(R.id.image_profile_pic);
        if (savedInstanceState != null) {
            recoverImage(savedInstanceState);
            teamsSelectedSet = savedInstanceState.getBoolean("teamsSelectedSet");
        }

        etProfileName = findViewById(R.id.et_profile_name);

        customAutoCompleteMajors =
                new CustomMultiAutoCompleteTextView((MultiAutoCompleteTextView) findViewById(R.id.tv_majors),
                        (TextInputLayout) findViewById(R.id.text_input_majors), R.string.empty_majors_input,
                        R.string.invalid_major_input, R.array.majors_array, this);


        autoCompleteTvYear = findViewById(R.id.tv_year);
        setupFauxSpinner(autoCompleteTvYear, R.array.year_array);

        customAutoCompleteTeams =
                new CustomMultiAutoCompleteTextView((MultiAutoCompleteTextView) findViewById(R.id.tv_teams),
                        (TextInputLayout) findViewById(R.id.text_input_teams), R.string.empty_teams_input,
                        R.string.invalid_team_input, null, this);

        final Observer<List<String>> teamNamesObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> teamNames) {
                if (teamNames != null) {
                    customAutoCompleteTeams.addAll(teamNames);

                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection();
                    }
                }
            }
        };
        profileViewModel.getTeamNames().observe(this, teamNamesObserver);

        autoCompleteTvTitle = findViewById(R.id.tv_title);
        setupFauxSpinner(autoCompleteTvTitle, R.array.title_array);

        etBio = findViewById(R.id.et_bio);

        final Observer<Member> memberObserver = new Observer<Member>() {
            @Override
            public void onChanged(@Nullable final Member member) {
                // Fill in profile fields with user's current info.
                if (member != null) {
                    GlideApp.with(ProfileActivity.this)
                            .load(member.getPhotoUrl())
                            .placeholder(Util.getThemedDrawable(R.attr.ic_profile, ProfileActivity.this))
                            .centerCrop()
                            .into(imgProfilePic);
                    etProfileName.setText(member.getName());
                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection();
                    }
                    customAutoCompleteMajors.fill(member.getMajors());

                    if (((ArrayAdapter) autoCompleteTvYear.getAdapter()).getPosition(member.getYear()) != -1) {
                        autoCompleteTvYear.setText(member.getYear());
                        ((ArrayAdapter) autoCompleteTvYear.getAdapter()).getFilter().filter(null);
                    }
                    if (((ArrayAdapter) autoCompleteTvTitle.getAdapter()).getPosition(member.getTitle()) != -1) {
                        autoCompleteTvTitle.setText(member.getTitle());
                        ((ArrayAdapter) autoCompleteTvTitle.getAdapter()).getFilter().filter(null);
                    }
                    etBio.setText(member.getBio());

                    profileViewModel.getMember().removeObserver(this);
                }
            }

        };
        if (savedInstanceState == null || savedInstanceState.getParcelable(CROPPED_IMAGE_FILE_URI) == null) {
            profileViewModel.getMember().observe(this, memberObserver);
        }
        Button btnSubmitChanges = findViewById(R.id.btn_submit_changes);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        textInputProfileName = findViewById(R.id.text_input_profile_name);
        textInputBio = findViewById(R.id.text_input_bio);
        textInputTitle = findViewById(R.id.text_input_title);
        textInputYear = findViewById(R.id.text_input_year);
        btnSubmitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> teamNamesInput = customAutoCompleteTeams.getInput();
                List<String> majorsInput = customAutoCompleteMajors.getInput();

                // Check user input and show warnings accordingly
                boolean warningShown = customAutoCompleteTeams.checkInput(teamNamesInput);
                warningShown = customAutoCompleteMajors.checkInput(majorsInput) || warningShown;

                String profileNameInput = etProfileName.getText().toString().trim();
                String bioInput = etBio.getText().toString().trim();
                String yearInput = autoCompleteTvYear.getText().toString();
                String titleInput = autoCompleteTvTitle.getText().toString();

                warningShown = checkEmptyInput(profileNameInput, textInputProfileName, R.string.empty_name_input) || warningShown;
                warningShown = checkEmptyInput(bioInput, textInputBio, R.string.empty_bio_input) || warningShown;
                warningShown = checkEmptyInput(yearInput, textInputYear, R.string.empty_year_input) || warningShown;
                warningShown = checkEmptyInput(titleInput, textInputTitle, R.string.empty_title_input) || warningShown;

                if (!warningShown) {
                    String uid = firebaseUser.getUid();
                    Member member = new Member(profileNameInput, uid, bioInput, teamNamesInput, yearInput, majorsInput, titleInput);
                    // Todo: Add listener to setMember to add progressBar as well as snackbar if failed to update profile
                    profileViewModel.setMember(member, croppedImageFileUri);
                    finish();
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
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE);

            }
        });

    }

    private void recoverImage(Bundle savedInstanceState) {
        croppedImageFileUri = savedInstanceState.getParcelable(CROPPED_IMAGE_FILE_URI);
        if (croppedImageFileUri != null) {
            GlideApp.with(ProfileActivity.this)
                    .load(croppedImageFileUri)
                    .placeholder(Util.getThemedDrawable(R.attr.ic_profile, this))
                    .centerCrop()
                    .into(imgProfilePic);
        }
    }


    void setTeamsSelection() {
        Member member = profileViewModel.getMember().getValue();
        if (member != null) {
            if (member.getTeams().size() != 0) {
                teamsSelectedSet = true;
                customAutoCompleteTeams.fill(member.getTeams());
            }

        }
    }

    private void setupFauxSpinner(final AutoCompleteTextView autoCompleteTextView, int stringArrayResource) {
        List<CharSequence> items = new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(stringArrayResource)));
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setKeyListener(null);

        final GestureDetector gestureDetector = new GestureDetector(ProfileActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                autoCompleteTextView.showDropDown();
                return true;
            }
        });

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private boolean checkEmptyInput(String input, TextInputLayout textInputLayout, int emptyErrorResource) {
        boolean warningShown = false;
        if (input.isEmpty()) {
            textInputLayout.setError(getString(emptyErrorResource));
            warningShown = true;
        } else {
            textInputLayout.setError(null);
        }
        return warningShown;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
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
            } else {
                Log.w(TAG, "PICK_IMAGE cancelled");
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                croppedImageFileUri = UCrop.getOutput(data);
                GlideApp.with(ProfileActivity.this)
                        .load(croppedImageFileUri)
                        .placeholder(Util.getThemedDrawable(R.attr.ic_profile, this))
                        .centerCrop()
                        .into(imgProfilePic);
            } else {
                if (data != null) {
                    Log.w(TAG, "REQUEST_CROP cancelled", UCrop.getError(data));
                } else {
                    Log.w(TAG, "REQUEST_CROP cancelled");
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (croppedImageFileUri != null) {
            outState.putParcelable(CROPPED_IMAGE_FILE_URI, croppedImageFileUri);
        }
        outState.putBoolean("teamsSelectedSet", teamsSelectedSet);
        super.onSaveInstanceState(outState);
    }
}
