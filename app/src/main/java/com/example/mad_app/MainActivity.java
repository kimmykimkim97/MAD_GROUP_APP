package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView animationView = findViewById(R.id.lv_logo);
        animationView.playAnimation(); // Start the animation

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        // Using Handler to delay the transition to the next activity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Load_activity.class);
            startActivity(intent);
            finish(); // Close the splash screen activity
        }, 4000); // Delay in milliseconds (3000ms = 3 seconds)

    }
}