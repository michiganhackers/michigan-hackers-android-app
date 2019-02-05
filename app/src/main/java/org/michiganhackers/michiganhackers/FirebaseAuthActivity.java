package org.michiganhackers.michiganhackers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.login.LoginActivity;
import org.michiganhackers.michiganhackers.login.SignupActivity;
import org.michiganhackers.michiganhackers.settings.SettingsViewModel;
import org.michiganhackers.michiganhackers.settings.SettingsViewModelFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import static org.michiganhackers.michiganhackers.login.LoginActivity.INTENT_FROM;
import static org.michiganhackers.michiganhackers.login.LoginActivity.USER_NOT_SIGNED_IN;
import static org.michiganhackers.michiganhackers.login.SignupActivity.FROM_ACCOUNT_DELETE;

public abstract class FirebaseAuthActivity extends AppCompatActivity {
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;
    protected FirebaseUser firebaseUser;
    private final String TAG = getClass().getCanonicalName();
    public static final int REQUIRE_USER_SIGNED_IN_REQUEST_CODE = 999;
    private SettingsViewModel settingsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                requireUserSignedIn(user);
            }
        };
        setFirebaseUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFirebaseUser();
    }

    private void setFirebaseUser() {
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            SettingsViewModelFactory settingsViewModelFactory = new SettingsViewModelFactory(firebaseUser.getUid());
            settingsViewModel = ViewModelProviders.of(this, settingsViewModelFactory).get(SettingsViewModel.class);
        } else {
            Log.w(TAG, "null user in onResume");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    // Returns true if user signed in and false if not
    protected void requireUserSignedIn(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(INTENT_FROM, USER_NOT_SIGNED_IN);
            startActivityForResult(intent, REQUIRE_USER_SIGNED_IN_REQUEST_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUIRE_USER_SIGNED_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recreate();
            } else {
                Log.w(TAG, "REQUIRE_USER_SIGNED_IN_REQUEST_CODE cancelled");
                finish();
            }
        }


    }

    public void signOut() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        finish();
        auth.signOut();
    }

    public void deleteAccount() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        settingsViewModel.removeMember(firebaseUser.getUid());
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(INTENT_FROM, FROM_ACCOUNT_DELETE);
        startActivity(intent);
        finish();
    }


}
