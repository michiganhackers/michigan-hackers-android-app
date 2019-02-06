package org.michiganhackers.michiganhackers.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.Util;
import org.michiganhackers.michiganhackers.login.LoginActivity;
import org.michiganhackers.michiganhackers.login.ResetPasswordActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class ChangePasswordFragment extends Fragment {
    private final String TAG = getClass().getCanonicalName();
    public static final int RESET_PASSWORD_REQUEST_CODE = 1;

    private TextInputLayout txtInputPwdOld, txtInputPwdNew, txtInputPwdConfirm;
    private TextInputEditText etInputPwdOld, etInputPwdNew, etInputPwdConfirm;
    private Button btnSubmit, btnResetPassword;
    private CoordinatorLayout coordinatorLayout;


    public ChangePasswordFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_change_password, container, false);

        if (getActivity() != null) {
            ((SettingsActivity) getActivity()).setToolbarTitle(R.string.change_password);
        }

        txtInputPwdOld = layout.findViewById(R.id.text_input_old_pwd);
        txtInputPwdNew = layout.findViewById(R.id.text_input_pwd);
        txtInputPwdConfirm = layout.findViewById(R.id.text_input_confirm_pwd);
        btnSubmit = layout.findViewById(R.id.btn_submit);
        coordinatorLayout = layout.findViewById(R.id.coordinator_layout);
        etInputPwdOld = layout.findViewById(R.id.et_old_pwd);
        etInputPwdNew = layout.findViewById(R.id.et_pwd);
        etInputPwdConfirm = layout.findViewById(R.id.et_confirm_pwd);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPwd = etInputPwdOld.getText().toString();
                final String pwdNew = etInputPwdNew.getText().toString();
                final String pwdConfirm = etInputPwdConfirm.getText().toString();


                Util.InputFieldObject oldPwdInputFieldObj = new Util.InputFieldObject(oldPwd, getString(R.string.enter_password), txtInputPwdOld);
                Util.InputFieldObject newPwdInputFieldObj = new Util.InputFieldObject(pwdNew, getString(R.string.enter_password), txtInputPwdNew);
                Util.InputFieldObject confirmPwdInputFieldObj = new Util.InputFieldObject(pwdConfirm, getString(R.string.enter_password), txtInputPwdConfirm);

                if (Util.isAnyInputFieldEmpty(oldPwdInputFieldObj, newPwdInputFieldObj, confirmPwdInputFieldObj)) {
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Log.e(TAG, "null user in onClick");

                }
                PasswordValidator passwordValidator = new PasswordValidator(oldPwd, user, txtInputPwdOld, getString(R.string.incorrect_password)) {
                    @Override
                    public void onSuccess() {
                        if (pwdNew.length() < 8) {
                            txtInputPwdNew.setError(getString(R.string.pwd_too_short));
                            txtInputPwdConfirm.setError(null);
                            return;
                        }
                        if (!pwdNew.equals(pwdConfirm)) {
                            txtInputPwdNew.setError(getString(R.string.passwords_dont_match));
                            txtInputPwdConfirm.setError(getString(R.string.passwords_dont_match));
                            return;
                        }

                        txtInputPwdNew.setError(null);
                        txtInputPwdConfirm.setError(null);
                        changePassword(user, pwdNew);

                    }

                    @Override
                    public void onFailure() {

                    }
                };
                passwordValidator.validatePassword();


            }
        });

        btnResetPassword = layout.findViewById(R.id.btn_reset_password);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ResetPasswordActivity.class), RESET_PASSWORD_REQUEST_CODE);
            }
        });

        return layout;
    }

    private void changePassword(FirebaseUser user, String pwdNew) {
        user.updatePassword(pwdNew)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(coordinatorLayout, getString(R.string.pwd_change_success), Snackbar.LENGTH_LONG).show();
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, getString(R.string.pwd_change_failure) + msg, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESET_PASSWORD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Snackbar.make(coordinatorLayout, R.string.pwd_reset_confirmation, Snackbar.LENGTH_LONG).show();
                } else {
                    Log.w(TAG, "RESET_PASSWORD_REQUEST_CODE cancelled");
                }
        }
    }
}

