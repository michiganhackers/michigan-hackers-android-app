package org.michiganhackers.michiganhackers.account;

import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.login.LoginActivity;
import org.michiganhackers.michiganhackers.profile.ProfileViewModel;
import org.michiganhackers.michiganhackers.profile.ProfileViewModelFactory;
import org.michiganhackers.michiganhackers.login.SignupActivity;


public class AccountActivity extends AppCompatActivity {

    private Button btnChangeEmail, btnChangePassword, btnSendPwdResetEmail, btnRemoveUser,
            btnConfirmChangeEmail, btnConfirmChangePwd, btnConfirmSendPwdResetEmail, btnConfirmRemove, btnSignOut;
    private EditText etPwdResetEmail, etNewEmail, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
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
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnChangeEmail = findViewById(R.id.btn_change_email);
        btnChangePassword = findViewById(R.id.btn_change_pwd);
        btnSendPwdResetEmail =findViewById(R.id.btn_send_pwd_reset_email);
        btnRemoveUser = findViewById(R.id.btn_remove_user);
        btnConfirmChangeEmail = findViewById(R.id.btn_confirm_change_email);
        btnConfirmChangePwd = findViewById(R.id.btn_confirm_change_pwd);
        btnConfirmSendPwdResetEmail = findViewById(R.id.btn_confirm_send_pwd_reset_email);
        btnConfirmRemove = findViewById(R.id.btn_confirm_remove_user);
        btnSignOut = findViewById(R.id.btn_sign_out);

        etPwdResetEmail = findViewById(R.id.et_pwd_reset_email);
        etNewEmail = findViewById(R.id.et_new_email);
        etPassword = findViewById(R.id.et_pwd);
        etConfirmPassword = findViewById(R.id.et_confirm_pwd);

        etPwdResetEmail.setVisibility(View.GONE);
        etNewEmail.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        etConfirmPassword.setVisibility(View.GONE);
        btnConfirmChangeEmail.setVisibility(View.GONE);
        btnConfirmChangePwd.setVisibility(View.GONE);
        btnConfirmSendPwdResetEmail.setVisibility(View.GONE);
        btnConfirmRemove.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progress_bar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPwdResetEmail.setVisibility(View.GONE);
                etNewEmail.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.GONE);
                etConfirmPassword.setVisibility(View.GONE);
                btnConfirmChangeEmail.setVisibility(View.VISIBLE);
                btnConfirmChangePwd.setVisibility(View.GONE);
                btnConfirmSendPwdResetEmail.setVisibility(View.GONE);
                btnConfirmRemove.setVisibility(View.GONE);
            }
        });

        btnConfirmChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && !etNewEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(etNewEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (user == null) {
                    Toast.makeText(AccountActivity.this, "Error with user", Toast.LENGTH_SHORT).show();
                } else if (etNewEmail.getText().toString().trim().equals("")) {
                    etNewEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPwdResetEmail.setVisibility(View.GONE);
                etNewEmail.setVisibility(View.GONE);
                etPassword.setVisibility(View.VISIBLE);
                etConfirmPassword.setVisibility(View.VISIBLE);
                btnConfirmChangeEmail.setVisibility(View.GONE);
                btnConfirmChangePwd.setVisibility(View.VISIBLE);
                btnConfirmSendPwdResetEmail.setVisibility(View.GONE);
                btnConfirmRemove.setVisibility(View.GONE);
            }
        });

        btnConfirmChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && !etPassword.getText().toString().trim().equals("") && !etConfirmPassword.getText().toString().trim().equals("")) {
                    if (etPassword.getText().toString().trim().length() < 6) {
                        etPassword.setError("Password too short, enter minimum 6 characters");
                        etConfirmPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else if (!etPassword.getText().equals(etConfirmPassword.getText())) {
                        etPassword.setError("Passwords do not match");
                        etConfirmPassword.setError("Passwords do not match");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(etPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AccountActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(AccountActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
                if (etPassword.getText().toString().trim().equals("")) {
                    etPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
                if (etConfirmPassword.getText().toString().trim().equals("")) {
                    etConfirmPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendPwdResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPwdResetEmail.setVisibility(View.VISIBLE);
                etNewEmail.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etConfirmPassword.setVisibility(View.GONE);
                btnConfirmChangeEmail.setVisibility(View.GONE);
                btnConfirmChangePwd.setVisibility(View.GONE);
                btnConfirmSendPwdResetEmail.setVisibility(View.VISIBLE);
                btnConfirmRemove.setVisibility(View.GONE);
            }
        });

        btnConfirmSendPwdResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!etPwdResetEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(etPwdResetEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    etPwdResetEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPwdResetEmail.setVisibility(View.GONE);
                etNewEmail.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etConfirmPassword.setVisibility(View.GONE);
                btnConfirmChangeEmail.setVisibility(View.GONE);
                btnConfirmChangePwd.setVisibility(View.GONE);
                btnConfirmSendPwdResetEmail.setVisibility(View.GONE);
                btnConfirmRemove.setVisibility(View.VISIBLE);
            }
        });

        btnConfirmRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    final String uid = user.getUid();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AccountActivity.this, SignupActivity.class));
                                        profileViewModel.removeMember(uid);
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(AccountActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(AccountActivity.this);
                }
                builder.setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
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
