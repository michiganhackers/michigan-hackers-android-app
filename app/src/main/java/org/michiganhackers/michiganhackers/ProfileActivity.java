package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private final static int PICK_IMAGE = 1;
    private static final String TAG = ProfileActivity.class.getName();

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
        final EditText majorEditText = findViewById(R.id.profile_major);
        final EditText yearEditText = findViewById(R.id.profile_year);
        final EditText teamEditText = findViewById(R.id.profile_team);
        final EditText titleEditText = findViewById(R.id.profile_title);
        final EditText bioEditText = findViewById(R.id.profile_bio);

        if(user != null){
            String uid = user.getUid();
            if(directoryViewModel.getMember(uid)!=null){
                Member member = directoryViewModel.getMember(uid);
                nameEditText.setText(member.getName());
                majorEditText.setText(member.getMajor());
                yearEditText.setText(member.getYear());
                teamEditText.setText(member.getTeam());
                titleEditText.setText(member.getTitle());
                bioEditText.setText(member.getBio());
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
                String teamName = teamEditText.getText().toString();
                String memberName = nameEditText.getText().toString();
                String major = majorEditText.getText().toString();
                String year = yearEditText.getText().toString();
                String title = titleEditText.getText().toString();
                String bio = bioEditText.getText().toString();
                if(user != null){
                    String uid = user.getUid();
                    Member member = new Member(memberName, uid, bio, teamName, year, major, title);
                    directoryViewModel.addMember(member);
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

/*                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);*/
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView profilePic = findViewById(R.id.profile_pic);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
