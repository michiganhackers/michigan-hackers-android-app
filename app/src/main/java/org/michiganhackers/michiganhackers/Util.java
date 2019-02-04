package org.michiganhackers.michiganhackers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.michiganhackers.michiganhackers.login.LoginActivity;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import static org.michiganhackers.michiganhackers.login.LoginActivity.INTENT_FROM;
import static org.michiganhackers.michiganhackers.login.LoginActivity.USER_NOT_SIGNED_IN;

public final class Util {
    private static final String TAG = "Util";

    // Private constructor to prevent instantiation
    private Util() {
    }

    public static Drawable getThemedDrawable(int attributeId, Context context) {
        TypedArray arr = context.getTheme().obtainStyledAttributes(R.style.BaseTheme, new int[]{attributeId});
        int resourceId = arr.getResourceId(0, 0);
        return context.getResources().getDrawable(resourceId);
    }

    // NOTE: Will ignore fields if they have null values
    public static Map pojoToMap(Object obj) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(obj);
        return new Gson().fromJson(json, Map.class);
    }
}