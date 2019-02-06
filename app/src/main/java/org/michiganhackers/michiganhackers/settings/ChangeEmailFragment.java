package org.michiganhackers.michiganhackers.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.Util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

public class ChangeEmailFragment extends Fragment {
    private final String TAG = getClass().getCanonicalName();

    private TextInputLayout textInputPassword, textInputEmail;
    private TextInputEditText etEmail, etPassword;
    private Button btnSubmit;
    private CoordinatorLayout coordinatorLayout;

    public ChangeEmailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_change_email, container, false);

        if (getActivity() != null) {
            ((SettingsActivity) getActivity()).setToolbarTitle(R.string.change_email);
        }

        textInputPassword = layout.findViewById(R.id.text_input_pwd);
        textInputEmail = layout.findViewById(R.id.text_input_email);
        etEmail = layout.findViewById(R.id.et_email);
        etPassword = layout.findViewById(R.id.et_pwd);
        btnSubmit = layout.findViewById(R.id.btn_submit);
        coordinatorLayout = layout.findViewById(R.id.coordinator_layout);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                Util.InputFieldObject emailInputFieldObj = new Util.InputFieldObject(email, getString(R.string.enter_email), textInputEmail);
                Util.InputFieldObject passwordInputFieldObj = new Util.InputFieldObject(password, getString(R.string.enter_password), textInputPassword);
                if (Util.isAnyInputFieldEmpty(emailInputFieldObj, passwordInputFieldObj)) {
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Log.e(TAG, "null user in onClick");
                }
                PasswordValidator passwordValidator = new PasswordValidator(password, user, textInputPassword, getString(R.string.incorrect_password)) {
                    @Override
                    public void onSuccess() {
                        updateEmail(user);
                    }

                    @Override
                    public void onFailure() {
                    }
                };
                passwordValidator.validatePassword();
            }
        });

        return layout;
    }

    private void updateEmail(final FirebaseUser user) {
        user.updateEmail(etEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(coordinatorLayout, getString(R.string.email_change_success), Snackbar.LENGTH_LONG).show();
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, getString(R.string.failed_to_update_email) + msg, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

