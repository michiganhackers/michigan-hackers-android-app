package org.michiganhackers.michiganhackers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

public class TEST_ACTIVITY extends AppCompatActivity {
    private RatingBar rateUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHandler themeHan = new ThemeHandler(this);
        themeHan.setTheme();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__activity);


        rateUs = findViewById(R.id.rateUs);
        rateUs.setNumStars(5);
        rateUs.isIndicator();

        final Toast ratingChangeToast = Toast.makeText(getApplicationContext(), "Thank you for rating us 5 stars!", Toast.LENGTH_SHORT);


        RatingBar.OnRatingBarChangeListener ratingslistener;

        ratingslistener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingChangeToast.show();
                rateUs.setRating(5);
            }
        };

        rateUs.setOnRatingBarChangeListener(ratingslistener);





    }
}
