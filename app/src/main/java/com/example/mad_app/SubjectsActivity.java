package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectsActivity extends AppCompatActivity {

    private LinearLayout containerSubjects;
    private Button btnNextSubjects;
    private int numberOfSubjects;
    private FirebaseFirestore db;

    private List<Map<String, String>> collectSubjectsData() {
        List<Map<String, String>> subjectsList = new ArrayList<>();
        for (int i = 0; i < containerSubjects.getChildCount(); i += 2) {
            EditText subjectInput = (EditText) containerSubjects.getChildAt(i);
            Spinner gradeSpinner = (Spinner) containerSubjects.getChildAt(i + 1);

            String subject = subjectInput.getText().toString();
            String grade = gradeSpinner.getSelectedItem().toString();

            Map<String, String> subjectData = new HashMap<>();
            subjectData.put("subject", subject);
            subjectData.put("grade", grade);
            subjectsList.add(subjectData);
        }
        return subjectsList;
    }

    private void addSubjectInputs(int count) {
        for (int i = 0; i < count; i++) {
            // Add EditText for subject name
            EditText subjectInput = new EditText(this);
            subjectInput.setHint("Subject " + (i + 1));
            subjectInput.setPadding(16, 16, 16, 16); // Add padding
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 8, 0, 8); // Add margin
            subjectInput.setLayoutParams(layoutParams);
            containerSubjects.addView(subjectInput);

            // Add Spinner for grade selection
            Spinner gradeSpinner = new Spinner(this);
            ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.grade_options,
                    android.R.layout.simple_spinner_item
            );
            gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gradeSpinner.setAdapter(gradeAdapter);
            gradeSpinner.setLayoutParams(layoutParams); // Apply same margins
            containerSubjects.addView(gradeSpinner);
        }
    }

    private boolean validateInputs() {
        for (int i = 0; i < containerSubjects.getChildCount(); i += 2) {
            EditText subjectInput = (EditText) containerSubjects.getChildAt(i);
            Spinner gradeSpinner = (Spinner) containerSubjects.getChildAt(i + 1);

            if (subjectInput.getText().toString().trim().isEmpty()) {
                subjectInput.setError("Field required");
                return false;
            }

            if (gradeSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a grade for each subject", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void saveDataToFirebase(String name, List<Map<String, String>> subjectsList) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("subjects", subjectsList);

        db.collection("users")
                .add(userData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(SubjectsActivity.this, "Data saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(SubjectsActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        db = FirebaseFirestore.getInstance();

        containerSubjects = findViewById(R.id.container_subjects);
        btnNextSubjects = findViewById(R.id.btn_next_subjects);

        // Get the number of subjects from the previous activity
        numberOfSubjects = getIntent().getIntExtra("number_of_subjects", 1);

        // Validate the number of subjects
        if (numberOfSubjects <= 0) {
            Toast.makeText(this, "Invalid number of subjects", Toast.LENGTH_SHORT).show();
            return; // Prevent further execution
        }

        // Dynamically add inputs
        addSubjectInputs(numberOfSubjects);

        btnNextSubjects.setOnClickListener(v -> {
            if (validateInputs()) {
                // Collect data
                List<Map<String, String>> subjectsList = collectSubjectsData();
                String name = getIntent().getStringExtra("name");

                // Save data to Firebase
                saveDataToFirebase(name, subjectsList);

                // Pass data to the next activity
                ArrayList<String> subjects = new ArrayList<>();
                ArrayList<String> grades = new ArrayList<>();
                for (Map<String, String> subject : subjectsList) {
                    subjects.add(subject.get("subject"));
                    grades.add(subject.get("grade"));
                }

                Intent intent = new Intent(SubjectsActivity.this, StudyScheduleActivity.class);
                intent.putStringArrayListExtra("subjects", subjects);
                intent.putStringArrayListExtra("grades", grades);
                startActivity(intent);
            }
        });
    }

}
