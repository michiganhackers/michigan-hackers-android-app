package org.michiganhackers.michiganhackers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.michiganhackers.michiganhackers.login.LoginActivity;

import java.util.Map;

import static org.michiganhackers.michiganhackers.login.LoginActivity.INTENT_FROM;
import static org.michiganhackers.michiganhackers.login.LoginActivity.USER_NOT_SIGNED_IN;

public final class Util {
    // Private constructor to prevent instantiation
    private Util() {
    }

    public static Drawable getThemedDrawable(int attributeId, Context context) {
        TypedArray arr = context.getTheme().obtainStyledAttributes(R.style.BaseTheme, new int[]{attributeId});
        int resourceId = arr.getResourceId(0, 0);
        return context.getResources().getDrawable(resourceId);
    }
//
//    public static boolean validatePassword(String password) {
//
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "User re-authenticated.");
//                        if(task.isSuccessful()){
//                            updateUserEmail();
//                        } else {
//                            // Password is incorrect
//                        }
//                    }
//                });
//    }


    // NOTE: Will ignore fields if they have null values
    public static Map pojoToMap(Object obj) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(obj);
        return new Gson().fromJson(json, Map.class);
    }

}
