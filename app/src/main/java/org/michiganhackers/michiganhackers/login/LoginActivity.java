package org.michiganhackers.michiganhackers.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.michiganhackers.MainActivity;
import org.michiganhackers.michiganhackers.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout textInputEmail, textInputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnResetPassword;
    private CoordinatorLayout coordinatorLayout;

    public static final int RESET_PASSWORD_REQUEST_CODE = 1;
    public static final int SIGNUP_REQUEST_CODE = 2;
    public static final String USER_NOT_SIGNED_IN = "User not signed in";
    public static final String INTENT_FROM = "Intent from";
    public static final String FROM_ACCOUNT_DELETE = "From account delete";
    private final String TAG = getClass().getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pwd);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        progressBar = findViewById(R.id.progress_bar);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, SIGNUP_REQUEST_CODE);
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivityForResult(intent, RESET_PASSWORD_REQUEST_CODE);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (isAnyInputFieldEmpty(email, password)) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Snackbar.make(coordinatorLayout, R.string.auth_failed_login, Snackbar.LENGTH_LONG).show();
                                } else {
                                    if(getIntent() != null && getIntent().getStringExtra(INTENT_FROM) != null && getIntent().getStringExtra(INTENT_FROM).equals(USER_NOT_SIGNED_IN)){
                                        setResult(Activity.RESULT_OK);
                                    }
                                    else{
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            }
                        });
            }
        });


        showIntentFromSnackbar();
    }

    private void showIntentFromSnackbar() {
        if (getIntent() != null && getIntent().getStringExtra(INTENT_FROM) != null) {
            switch (getIntent().getStringExtra(INTENT_FROM)) {
                case USER_NOT_SIGNED_IN:
                    Snackbar.make(coordinatorLayout, R.string.signed_in_required_messsage, Snackbar.LENGTH_LONG).show();
                    break;
                case FROM_ACCOUNT_DELETE:
                    Snackbar.make(coordinatorLayout, R.string.account_deleted_message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESET_PASSWORD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Snackbar.make(coordinatorLayout, R.string.pwd_reset_confirmation, Snackbar.LENGTH_LONG).show();
                } else {
                    Log.w(TAG, "RESET_PASSWORD_REQUEST_CODE cancelled");
                }
                break;
            case SIGNUP_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    finish();
                } else {
                    Log.w(TAG, "SIGNUP_REQUEST_CODE cancelled");
                }
        }
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