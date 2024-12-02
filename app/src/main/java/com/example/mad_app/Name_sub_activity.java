package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Name_sub_activity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerSubjects;
    private Button btnNext;
    private TextView tvmsg;
    private int selectedNumberOfSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_sub);

        etName = findViewById(R.id.et_name);
        spinnerSubjects = findViewById(R.id.spinner_subjects);
        btnNext = findViewById(R.id.btn_next);
        tvmsg = findViewById(R.id.tv_namesub);

        String pageText = "Just input your grades, and we'll guide you with optimized plans to stay on track and achieve success.";
        tvmsg.setText(pageText);

        // Populate the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.subject_numbers,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        // Handle selection
        spinnerSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNumberOfSubjects = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedNumberOfSubjects = 1; // Default value
            }
        });

        // Handle button click
        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Please enter your name");
            } else {
                // Pass data to the next activity
                Intent intent = new Intent(Name_sub_activity.this, SubjectsActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("number_of_subjects", selectedNumberOfSubjects);
                startActivity(intent);
            }
        });
    }
}

