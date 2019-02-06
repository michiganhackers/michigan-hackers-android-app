package org.michiganhackers.michiganhackers.settings;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.Util;
import org.michiganhackers.michiganhackers.directory.DirectoryViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class DeleteAccountFragment extends Fragment {
    private final String TAG = getClass().getCanonicalName();

    private TextInputLayout textInputPassword;
    private TextInputEditText etPassword;
    private Button btnDeleteAcc;

    public DeleteAccountFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_delete_account, container, false);

        if (getActivity() != null) {
            ((SettingsActivity) getActivity()).setToolbarTitle(R.string.delete_account);
        }

        textInputPassword = layout.findViewById(R.id.text_input_pwd);
        etPassword = layout.findViewById(R.id.et_pwd);
        btnDeleteAcc = layout.findViewById(R.id.btn_delete_account);

        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = etPassword.getText().toString();

                Util.InputFieldObject passwordInputFieldObj = new Util.InputFieldObject(password, getString(R.string.enter_password), textInputPassword);
                if (Util.isAnyInputFieldEmpty(passwordInputFieldObj)) {
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Log.e(TAG, "null user in onClick");
                }
                PasswordValidator passwordValidator = new PasswordValidator(password, user, textInputPassword, getString(R.string.incorrect_password)) {
                    @Override
                    public void onSuccess() {
                        showDeleteAccountDialog();
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

    private void showDeleteAccountDialog() {
        if (getActivity() == null) {
            Log.e(TAG, "getActivity() == null in showDeleteAccountDialog()");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        builder.setTitle(R.string.delete_account)
                .setMessage(R.string.account_delete_warning_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            ((FirebaseAuthActivity) getActivity()).deleteAccount();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}

