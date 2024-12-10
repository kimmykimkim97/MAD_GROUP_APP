package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Intent intent = new Intent(ProgressActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}