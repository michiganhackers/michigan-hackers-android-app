package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
        ArrayList<String> majorSpinnerItems = directoryViewModel.getMajors();
        majorSpinnerItems.add(getString(R.string.add_major_spinner_item));
        final ArrayAdapter<String> majorSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorSpinnerItems);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorSpinnerAdapter);
        majorSpinner.setOnItemSelectedListener(getSpinnerListenerForCustomText(getString(R.string.add_major_spinner_item), new OnSpinnerItemSelected() {
            @Override
            public void addTextToDirectory(String text) {
                directoryViewModel.addMajor(text);
            }
        }));
        
        final Spinner yearSpinner = findViewById(R.id.profile_year);
        final ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearSpinnerAdapter);

        final Spinner teamSpinner = findViewById(R.id.profile_team);
        ArrayList<String> teamSpinnerItems = directoryViewModel.getTeams();
        teamSpinnerItems.add(getString(R.string.add_team_spinner_item));
        final ArrayAdapter<String> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamSpinnerItems);
        teamSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(teamSpinnerAdapter);
        teamSpinner.setOnItemSelectedListener(getSpinnerListenerForCustomText(getString(R.string.add_team_spinner_item), new OnSpinnerItemSelected() {
            @Override
            public void addTextToDirectory(String text) {
                // todo add team here
            }
        }));

        final Spinner titleSpinner = findViewById(R.id.profile_title);
        final ArrayAdapter<CharSequence> titleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.title_array, android.R.layout.simple_spinner_item);
        titleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleSpinnerAdapter);

        final EditText bioEditText = findViewById(R.id.profile_bio);
        final ImageView profilePic = findViewById(R.id.profile_pic);

        // Fill in editTexts with user's current info
        if(user != null){
            final String uid = user.getUid();
            Member member = directoryViewModel.getMember(uid);
            // member is initially null, but not after configuration changes
            if(member != null){
                nameEditText.setText(member.getName());
                majorSpinner.setSelection(majorSpinnerAdapter.getPosition(member.getMajor()));
                yearSpinner.setSelection(yearSpinnerAdapter.getPosition(member.getYear()));
                teamSpinner.setSelection(teamSpinnerAdapter.getPosition(member.getTeam()));
                titleSpinner.setSelection(titleSpinnerAdapter.getPosition(member.getTitle()));
                bioEditText.setText(member.getBio());
                GlideApp.with(this)
                        .load(member.getPhotoUrl())
                        .placeholder(R.drawable.ic_directory)
                        .centerCrop()
                        .into(profilePic);
            }
            // If member does not exist, observe for when teamsByName is updated (completely) and check again
            // Todo: How could this be null if it was made in the constructor for directoryViewModel?
            else if(!directoryViewModel.getTeamsByNameUpdated().getValue()) {
                final Observer<Boolean> teamsByNameUpdatedObserver = new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable final Boolean teamsByNameUpdated) {
                        Member member = directoryViewModel.getMember(uid);
                        if(member != null){
                            nameEditText.setText(member.getName());
                            majorSpinner.setSelection(majorSpinnerAdapter.getPosition(member.getMajor()));
                            yearSpinner.setSelection(yearSpinnerAdapter.getPosition(member.getYear()));
                            teamSpinner.setSelection(teamSpinnerAdapter.getPosition(member.getTeam()));
                            titleSpinner.setSelection(titleSpinnerAdapter.getPosition(member.getTitle()));
                            bioEditText.setText(member.getBio());
                            GlideApp.with(ProfileActivity.this)
                                    .load(member.getPhotoUrl())
                                    .placeholder(R.drawable.ic_directory)
                                    .centerCrop()
                                    .into(profilePic);
                        }
                    }
                };
                directoryViewModel.getTeamsByNameUpdated().observe(this, teamsByNameUpdatedObserver);
            }
        }
        else
        {
            Log.e(TAG, "Null user onStart");
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

    private AdapterView.OnItemSelectedListener getSpinnerListenerForCustomText(final String selectedText, final OnSpinnerItemSelected onSpinnerItemSelected){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().toString().equals(selectedText)){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ProfileActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ProfileActivity.this);
                    }
                    final EditText inputEditText = new EditText(ProfileActivity.this);
                    inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(inputEditText);
                    builder.setTitle(selectedText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String inputText = inputEditText.getText().toString();
                                    if(!inputText.equals("")){
                                        onSpinnerItemSelected.addTextToDirectory(inputText);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

}
