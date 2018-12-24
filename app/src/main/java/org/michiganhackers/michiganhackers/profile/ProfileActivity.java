package org.michiganhackers.michiganhackers.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import org.michiganhackers.michiganhackers.GlideApp;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.SemicolonTokenizer;
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
    private static final int PICK_IMAGE = 1;
    private static final String TAG = ProfileActivity.class.getName();
    private Uri croppedImageFileUri;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Boolean teamsSelectedSet = false;
    private ProfileViewModel profileViewModel;

    private TextInputEditText etProfileName, etBio;
    private AutoCompleteTextView autoCompleteTvYear, autoCompleteTvTitle;
    private MultiAutoCompleteTextView autoCompleteTvTeams, autoCompleteTvMajors;
    private TextInputLayout textInputProfileName, textInputMajors, textInputTeams, textInputBio, textInputTitle, textInputYear;
    private ImageView imgProfilePic;
    private CoordinatorLayout coordinatorLayout;

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

        imgProfilePic = findViewById(R.id.image_profile_pic);
        if (savedInstanceState != null) {
            croppedImageFileUri = savedInstanceState.getParcelable("croppedImageFileUri");
            if (croppedImageFileUri != null) {
                GlideApp.with(ProfileActivity.this)
                        .load(croppedImageFileUri)
                        .placeholder(R.drawable.ic_directory)
                        .centerCrop()
                        .into(imgProfilePic);
            } else {
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
        setupMultiAutoCompleteTextView(autoCompleteTvMajors, R.array.majors_array);

        autoCompleteTvYear = findViewById(R.id.tv_year);
        setupFauxSpinner(autoCompleteTvYear, R.array.year_array);

        autoCompleteTvTeams = findViewById(R.id.tv_teams);
        setupMultiAutoCompleteTextView(autoCompleteTvTeams, null);
        final Observer<List<String>> teamNamesObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> teamNames) {
                if (teamNames != null) {
                    // Populate team spinner
                    ArrayAdapter adapter = (ArrayAdapter) autoCompleteTvTeams.getAdapter();
                    adapter.clear();
                    adapter.addAll(teamNames);
                    adapter.notifyDataSetChanged();

                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection(adapter);
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
                            .placeholder(R.drawable.ic_directory)
                            .centerCrop()
                            .into(imgProfilePic);
                    etProfileName.setText(member.getName());
                    // Display the user's previous team selection if it hasn't been yet
                    if (!teamsSelectedSet) {
                        setTeamsSelection((ArrayAdapter) autoCompleteTvTeams.getAdapter());
                    }
                    for (String major : member.getMajors()) {
                        if (((ArrayAdapter) autoCompleteTvMajors.getAdapter()).getPosition(major) != -1) {
                            String newText = autoCompleteTvMajors.getText().toString() + major + "; ";
                            autoCompleteTvMajors.setText(newText);
                        }
                    }
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
        if (savedInstanceState == null) {
            profileViewModel.getMember().observe(this, memberObserver);
        }
        Button btnSubmitChanges = findViewById(R.id.btn_submit_changes);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        textInputProfileName = findViewById(R.id.text_input_profile_name);
        textInputMajors = findViewById(R.id.text_input_majors);
        textInputTeams = findViewById(R.id.text_input_teams);
        textInputBio = findViewById(R.id.text_input_bio);
        textInputTitle = findViewById(R.id.text_input_title);
        textInputYear = findViewById(R.id.text_input_year);
        btnSubmitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> teamNamesInput = getMultiAutoCompleteTextViewInput(autoCompleteTvTeams);
                List<String> majorsInput = getMultiAutoCompleteTextViewInput(autoCompleteTvMajors);

                // Check user input and show warnings accordingly
                Boolean warningShown = checkMultiAutoCompleteTextViewInput(teamNamesInput, autoCompleteTvTeams, textInputTeams, R.string.empty_teams_input, R.string.invalid_team_input);
                warningShown = checkMultiAutoCompleteTextViewInput(majorsInput, autoCompleteTvMajors, textInputMajors, R.string.empty_majors_input, R.string.invalid_major_input) || warningShown;

                String profileNameInput = etProfileName.getText().toString().trim();
                String bioInput = etBio.getText().toString().trim();
                String yearInput = autoCompleteTvYear.getText().toString();
                String titleInput = autoCompleteTvTitle.getText().toString();

                warningShown = checkEmptyInput(profileNameInput, textInputProfileName, R.string.empty_name_input) || warningShown;
                warningShown = checkEmptyInput(bioInput, textInputBio, R.string.empty_bio_input) || warningShown;
                warningShown = checkEmptyInput(yearInput, textInputYear, R.string.empty_year_input) || warningShown;
                warningShown = checkEmptyInput(titleInput, textInputTitle, R.string.empty_title_input) || warningShown;

                if (!warningShown) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        Member member = new Member(profileNameInput, uid, bioInput, teamNamesInput, yearInput, majorsInput, titleInput);
                        // Todo: Add listener to setMember to add progressBar as well as snackbar if failed to update profile
                        profileViewModel.setMember(member, croppedImageFileUri);
                        finish();
                    } else {
                        Snackbar.make(coordinatorLayout, R.string.profile_update_failed, Snackbar.LENGTH_LONG).show();
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
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE);

            }
        });

    }

    private void setupMultiAutoCompleteTextView(MultiAutoCompleteTextView multiAutoCompleteTextView, Integer stringArrayResource) {
        List<CharSequence> items = new ArrayList<>();
        if (stringArrayResource != null) {
            items.addAll(Arrays.asList(getResources().getStringArray(stringArrayResource)));
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new SemicolonTokenizer());
    }

    void setTeamsSelection(ArrayAdapter<CharSequence> teamsMultiAutoCompleteTextViewAdapter) {
        Member member = profileViewModel.getMember().getValue();
        if (member != null) {
            if (member.getTeams().size() != 0) {
                teamsSelectedSet = true;
                for (String team : member.getTeams()) {
                    if (teamsMultiAutoCompleteTextViewAdapter.getPosition(team) != -1) {
                        String newText = autoCompleteTvTeams.getText().toString() + team + "; ";
                        autoCompleteTvTeams.setText(newText);
                    } else {
                        teamsSelectedSet = false;
                    }
                }
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

    private List<String> getMultiAutoCompleteTextViewInput(MultiAutoCompleteTextView multiAutoCompleteTextView) {
        List<String> list = new ArrayList<>(Arrays.asList(multiAutoCompleteTextView.getText().toString().trim().split("\\s*;\\s*")));
        // remove duplicates
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        // if input is empty, list will have 1 empty string
        if (list.size() == 1 && list.get(0).isEmpty()) {
            list.remove(0);
        }
        return list;
    }

    private Boolean checkMultiAutoCompleteTextViewInput(List<String> inputList, MultiAutoCompleteTextView multiAutoCompleteTextView, TextInputLayout textInputLayout, int emptyErrorResource, int invalidErrorSuffixResource) {
        Boolean warningShown = false;
        if (inputList.size() == 0) {
            textInputLayout.setError(getString(emptyErrorResource));
            warningShown = true;
        } else {
            for (String item : inputList) {
                if (((ArrayAdapter) multiAutoCompleteTextView.getAdapter()).getPosition(item) == -1) {
                    textInputLayout.setError("\"" + item + "\" " + getString(invalidErrorSuffixResource));
                    warningShown = true;
                    break;
                }
            }
        }
        if (!warningShown) {
            textInputLayout.setError(null);
        }
        return warningShown;
    }

    private Boolean checkEmptyInput(String input, TextInputLayout textInputLayout, int emptyErrorResource) {
        Boolean warningShown = false;
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
                        .placeholder(R.drawable.ic_directory)
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
            outState.putParcelable("croppedImageFileUri", croppedImageFileUri);
        }
        outState.putBoolean("teamsSelectedSet", teamsSelectedSet);
        super.onSaveInstanceState(outState);
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
