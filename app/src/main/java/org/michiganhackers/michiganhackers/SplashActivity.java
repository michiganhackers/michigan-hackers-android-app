package org.michiganhackers.michiganhackers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashActivity extends AppCompatActivity {
    private static final String NEW_USER = "NEW_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView imageViewLogo = findViewById(R.id.image_logo);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isNewUser = sharedPreferences.getBoolean(NEW_USER, true);
        if (isNewUser) {
            sharedPreferences.edit().putBoolean(NEW_USER, false).apply();

            TextView textViewMichigan = findViewById(R.id.tv_michigan);
            TextView textViewHackers = findViewById(R.id.tv_hackers);

            ObjectAnimator objectAnimatorTextViewMichigan = ObjectAnimator.ofFloat(textViewMichigan, "alpha", 0, 1f);
            ObjectAnimator objectAnimatorTextViewHackers = ObjectAnimator.ofFloat(textViewHackers, "alpha", 0, 1f);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Drawable drawableLogoForeground = getResources().getDrawable(R.mipmap.ic_launcher_foreground);
            float yDist = displayMetrics.heightPixels / 2 + drawableLogoForeground.getIntrinsicHeight() / 2;

            final ObjectAnimator objectAnimatorImageViewLogo = ObjectAnimator.ofFloat(imageViewLogo, "translationY", -1 * yDist, 0f);

            objectAnimatorImageViewLogo.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 600);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            AnimatorSet animSetWords = new AnimatorSet();
            animSetWords.playTogether(objectAnimatorTextViewMichigan, objectAnimatorTextViewHackers);

            animSetWords.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageViewLogo.setVisibility(View.VISIBLE);
                    objectAnimatorImageViewLogo.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animSetWords.setDuration(700).start();


        } else {
            imageViewLogo.setVisibility(View.VISIBLE);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
