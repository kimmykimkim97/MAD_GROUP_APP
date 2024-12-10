package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class Load_activity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        LottieAnimationView animationView = findViewById(R.id.lv_loading);
        animationView.playAnimation(); // Start the animation

        // Using Handler to delay the transition to the next activity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Load_activity.this, Startpage_activity.class);
            startActivity(intent);
        }, 4000); // Delay in milliseconds (4000ms = 4 seconds)
    }
}