package org.michiganhackers.michiganhackers.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.Util;
import org.michiganhackers.michiganhackers.login.LoginActivity;
import org.michiganhackers.michiganhackers.login.SignupActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class ChangeEmailFragment extends Fragment {
    private final String TAG = getClass().getCanonicalName();
    private static final String actionBarTitle = "Change Email";

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

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(actionBarTitle);
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
                if (getActivity() == null) {
                    Log.e(TAG, "getActivity() == null in btnSubmit onClick()");
                    return;
                }

                String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (isAnyInputFieldEmpty(email, password)) {
                    return;
                }

                final FirebaseAuth auth = ((FirebaseAuthActivity) getActivity()).auth;
                FirebaseUser user = auth.getCurrentUser();

                user.updateEmail(etEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar.make(coordinatorLayout, R.string.failed_to_update_email, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        return layout;
    }

    private boolean isAnyInputFieldEmpty(String email, String password) {
        boolean warningShown = false;
        if (TextUtils.isEmpty(email)) {
            textInputEmail.setError(getString(R.string.enter_email));
            warningShown = true;
        } else {
            textInputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            textInputPassword.setError(getString(R.string.enter_password));
            warningShown = true;
        } else {
            textInputPassword.setError(null);
        }

        return warningShown;
    }
}

