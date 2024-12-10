package com.example.mad_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameSubActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerSubjects;
    private Button btnNext;
    private TextView tvmsg;
    private int selectedNumberOfSubjects;

    private DatabaseReference databaseReference; // Firebase Database reference

    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_sub);

        etName = findViewById(R.id.et_name);
        spinnerSubjects = findViewById(R.id.spinner_subjects);
        btnNext = findViewById(R.id.btn_next);
        tvmsg = findViewById(R.id.tv_namesub);

        // Initialize database reference with correct URL
        databaseReference = FirebaseDatabase.getInstance("https://gradesync-790d0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

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

        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Please enter your name");
            } else {
                // Save to SharedPreferences
                saveToSharedPreferences("name", name);
                saveToSharedPreferences("number_of_subjects", String.valueOf(selectedNumberOfSubjects));

                // Firebase logic remains unchanged
                String userId = databaseReference.push().getKey();
                User user = new User(name, selectedNumberOfSubjects);

                if (userId != null) {
                    databaseReference.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                // Navigate to the next activity
                                Intent intent = new Intent(NameSubActivity.this, SubjectsActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("number_of_subjects", selectedNumberOfSubjects);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> etName.setError("Error saving data: " + e.getMessage()));
                }
            }
        });


    }
}
