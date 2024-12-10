package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    CardView scheduleCard, updateGradeCard, progressCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Bind views
        scheduleCard = findViewById(R.id.cv_schedule);
        updateGradeCard = findViewById(R.id.cv_updateGrade);
        progressCard = findViewById(R.id.cv_progress);

        // Set click listeners
        scheduleCard.setOnClickListener(v -> openScheduleActivity());
        updateGradeCard.setOnClickListener(v -> openUpdateGradeActivity());
        progressCard.setOnClickListener(v -> openProgressActivity());
    }

    private void openScheduleActivity() {
        // Intent to navigate to the Schedule activity
        Intent intent = new Intent(this, MyScheduleActivity.class);
        startActivity(intent);
    }

    private void openUpdateGradeActivity() {
        Intent intent = new Intent(this, UpdateGradesActivity.class);
        startActivity(intent);
    }

    private void openProgressActivity() {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

}