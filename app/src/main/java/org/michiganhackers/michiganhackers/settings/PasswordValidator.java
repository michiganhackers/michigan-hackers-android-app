package org.michiganhackers.michiganhackers.settings;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.R;

import androidx.annotation.NonNull;

public abstract class PasswordValidator {
    private final String TAG = getClass().getCanonicalName();
    private String password;
    private FirebaseUser firebaseUser;
    private TextInputLayout passwordTextInputLayout;
    private Context context;

    public abstract void onSuccess();

    public abstract void onFailure();

    public PasswordValidator(String password, FirebaseUser firebaseUser,
                             TextInputLayout passwordTextInputLayout, Context context) {
        this.password = password;
        this.firebaseUser = firebaseUser;
        if (firebaseUser == null) {
            Log.e(TAG, "null user in PasswordValidator");

        }
        this.passwordTextInputLayout = passwordTextInputLayout;
        this.context = context;
    }

    public void validatePassword() {
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            passwordTextInputLayout.setError(null);
                            onSuccess();
                        } else {
                            passwordTextInputLayout.setError((context.getString(R.string.incorrect_password)));
                            onFailure();
                        }
                    }
                });
    }
}
