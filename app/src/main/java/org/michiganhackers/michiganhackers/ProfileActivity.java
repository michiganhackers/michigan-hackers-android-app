package org.michiganhackers.michiganhackers;

import android.app.ActionBar;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity{
    private final static int PICK_IMAGE = 1;
    private static final String TAG = ProfileActivity.class.getName();
    private Uri croppedImageFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final DirectoryViewModel directoryViewModel = ViewModelProviders.of(this).get(DirectoryViewModel.class);

        //get firebase auth instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final EditText nameEditText = findViewById(R.id.profile_name);

        final Spinner majorSpinner = findViewById(R.id.profile_major);
        final List<String> majorSpinnerItems = new ArrayList<>();
        majorSpinnerItems.add(getString(R.string.add_major_spinner_item));
        final ArrayAdapter<String> majorSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorSpinnerItems);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(new NothingSelectedSpinnerAdapter(majorSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_major_hint),this));
        final MutableInteger prevMajorSpinnerPosition = new MutableInteger(majorSpinner.getSelectedItemPosition());
        majorSpinner.setOnItemSelectedListener(getSpinnerListenerForCustomText(getString(R.string.add_major_spinner_item), majorSpinnerItems, majorSpinnerAdapter, prevMajorSpinnerPosition));
        final Observer<List<String>> majorsObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> majors) {
                if (majors != null){
                    majorSpinnerItems.clear();
                    majorSpinnerItems.addAll(majors);
                    majorSpinnerItems.add(getString(R.string.add_major_spinner_item));
                    majorSpinnerAdapter.notifyDataSetChanged();
                }
                directoryViewModel.getMajors().removeObserver(this);
            }
        };
        if(savedInstanceState == null)
        {
            directoryViewModel.getMajors().observe(this, majorsObserver);
        }

        
        final Spinner yearSpinner = findViewById(R.id.profile_year);
        final ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(new NothingSelectedSpinnerAdapter(yearSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_year_hint),this));

        final Spinner teamSpinner = findViewById(R.id.profile_team);
        List<String> teamSpinnerItems = new ArrayList<>(); // todo: need to use observable
        teamSpinnerItems.add(getString(R.string.add_team_spinner_item));
        final ArrayAdapter<String> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamSpinnerItems);
        teamSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(new NothingSelectedSpinnerAdapter(teamSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_team_hint),this));
        final MutableInteger prevTeamSpinnerPosition = new MutableInteger(teamSpinner.getSelectedItemPosition());
        teamSpinner.setOnItemSelectedListener(getSpinnerListenerForCustomText(getString(R.string.add_team_spinner_item), teamSpinnerItems, teamSpinnerAdapter, prevTeamSpinnerPosition));

        final Spinner titleSpinner = findViewById(R.id.profile_title);
        final ArrayAdapter<CharSequence> titleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.title_array, android.R.layout.simple_spinner_item);
        titleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(new NothingSelectedSpinnerAdapter(titleSpinnerAdapter, R.layout.profile_spinner_row_nothing_selected, getString(R.string.select_title_hint),this));

        final EditText bioEditText = findViewById(R.id.profile_bio);
        final ImageView profilePic = findViewById(R.id.profile_pic);

        // Fill in editTexts with user's current info
        if(user != null){
            final String uid = user.getUid();
            final Observer<Map<String, Team>> teamsByNameObserver = new Observer<Map<String, Team>>() {
                @Override
                public void onChanged(@Nullable final Map<String, Team> teamsByName) {
                    Member member = directoryViewModel.getMember(uid);
                    if(member != null){
                        nameEditText.setText(member.getName());
                        if(majorSpinnerAdapter.getPosition(member.getMajor()) != -1)
                        {
                            majorSpinner.setSelection(majorSpinnerAdapter.getPosition(member.getMajor()));
                            prevMajorSpinnerPosition.setValue(majorSpinner.getSelectedItemPosition());
                        }
                        if(yearSpinnerAdapter.getPosition(member.getYear()) != -1){
                            yearSpinner.setSelection(yearSpinnerAdapter.getPosition(member.getYear()));
                        }
                        if(teamSpinnerAdapter.getPosition(member.getTeam()) != -1){
                            teamSpinner.setSelection(teamSpinnerAdapter.getPosition(member.getTeam()));
                            prevTeamSpinnerPosition.setValue(teamSpinner.getSelectedItemPosition());
                        }
                        if(titleSpinnerAdapter.getPosition(member.getTitle()) != -1){
                            titleSpinner.setSelection(titleSpinnerAdapter.getPosition(member.getTitle()));
                        }
                        bioEditText.setText(member.getBio());
                        GlideApp.with(ProfileActivity.this)
                                .load(member.getPhotoUrl())
                                .placeholder(R.drawable.ic_directory)
                                .centerCrop()
                                .into(profilePic);
                        directoryViewModel.getTeamsByName().removeObserver(this);
                    }
                }
            };
            if(savedInstanceState == null)
            {
                directoryViewModel.getTeamsByName().observe(this, teamsByNameObserver);
            }
        }
        else
        {
            Log.e(TAG, "Null user onCreate");
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
                if(user != null){
                    String uid = user.getUid();
                    Member member = new Member(memberName, uid, bio, teamName, year, major, title);
                    directoryViewModel.addMember(member, croppedImageFileUri);
                    finish();
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Null user submitChangesButton");
                }
            }
        });

        FloatingActionButton imageEditButton = findViewById(R.id.profile_imageEditButton);
        imageEditButton.setOnClickListener(new View.OnClickListener(){
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
                UCrop.of(sourceImageFileUri, destinationImageFileUri).withAspectRatio(1,1).start(ProfileActivity.this);
            } catch (IOException e) {
                Log.e(TAG, "Error while creating profilePic temp file", e);
            }
        }
        else if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            croppedImageFileUri = resultUri;
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageFileUri);
                ImageView profilePic = findViewById(R.id.profile_pic);
                profilePic.setImageBitmap(bitmap);
            }
            catch(IOException e){
                Log.e(TAG, "Error while converting profilePicCropped to bitmap", e);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(TAG, "Error cropping image", cropError);
        }
    }

    private AdapterView.OnItemSelectedListener getSpinnerListenerForCustomText(final String selectedText, final List<String> spinnerItems, final ArrayAdapter<String> adapter, final MutableInteger prevSpinnerPosition){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                //&& !parent.getItemAtPosition(prevSpinnerPosition.getValue()).toString().equals(selectedText)
                if(parent.getSelectedItem() != null && parent.getSelectedItem().toString().equals(selectedText)){
                    final View alertDialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.profile_spinner_alert_dialog,null);
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ProfileActivity.this, android.R.style.Theme_Material_Light_Dialog);
                    } else {
                        builder = new AlertDialog.Builder(ProfileActivity.this);
                    }
                    final EditText inputEditText = alertDialogView.findViewById(R.id.alertDialog_editText);
                    builder.setView(alertDialogView);
                    builder.setTitle(selectedText)
                            // Listener set to null because we override OnClick later
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener(){
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    parent.setSelection(prevSpinnerPosition.getValue());
                                }
                            });

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String inputText = inputEditText.getText().toString();
                            if(!inputText.equals("")){
                                spinnerItems.add(1, inputText);
                                adapter.notifyDataSetChanged();
                                prevSpinnerPosition.setValue(1);
                                dialog.dismiss();
                            }
                            else
                            {
                                inputEditText.setError(getString(R.string.new_major_empty_string_error));
                            }
                        }
                    });
                }
                else if(parent.getSelectedItem() != null)
                {
                    prevSpinnerPosition.setValue(parent.getSelectedItemPosition());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
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
}
