package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Startpage_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstart);

        Button btGetStarted = findViewById(R.id.bt_getStarted);
        TextView tvWelcome = findViewById(R.id.tv_welcm);
        TextView tvWelcomeMsg = findViewById(R.id.tv_wlcm2);
        String welcomMsg = "Welcome to GradeSync!\n"
                + "Your journey to better study habits starts here. "
                + "GradeSync helps you create personalized study schedules tailored to your current grades and academic goals. ";
        tvWelcomeMsg.setText(welcomMsg);

        btGetStarted.setOnClickListener(view -> {
            Intent intent = new Intent(Startpage_activity.this,NameSubActivity.class);
            startActivity(intent);
        });


    }
}