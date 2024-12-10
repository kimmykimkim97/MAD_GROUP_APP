package com.example.mad_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectsActivity extends AppCompatActivity {

    private LinearLayout containerSubjects;
    private Button btnNextSubjects;
    private int numberOfSubjects;

    private DatabaseReference databaseReference;

    private void saveSubjectsToSharedPreferences(List<Map<String, String>> subjectsList) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save subject data as JSON string
        Gson gson = new Gson();
        String json = gson.toJson(subjectsList);
        editor.putString("subjects", json);
        editor.apply();
    }


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
            EditText subjectInput = new EditText(this);
            subjectInput.setHint("Subject " + (i + 1));
            Spinner gradeSpinner = new Spinner(this);
            gradeSpinner.setBackgroundResource(R.drawable.tv_rcorner);

            ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.grade_options,
                    android.R.layout.simple_spinner_item
            );
            gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gradeSpinner.setAdapter(gradeAdapter);

            containerSubjects.addView(subjectInput);
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
                Toast.makeText(this, "Please select valid options for all subjects", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void saveDataToFirebase(String name, List<Map<String, String>> subjectsList) {
        DatabaseReference userRef = databaseReference.child("users").push();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("subjects", subjectsList);

        userRef.setValue(userData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(SubjectsActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(SubjectsActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        databaseReference = FirebaseDatabase.getInstance("https://gradesync-790d0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        containerSubjects = findViewById(R.id.container_subjects);
        btnNextSubjects = findViewById(R.id.btn_next_subjects);

        numberOfSubjects = getIntent().getIntExtra("number_of_subjects", 1);
        addSubjectInputs(numberOfSubjects);

        btnNextSubjects.setOnClickListener(v -> {
            if (validateInputs()) {
                List<Map<String, String>> subjectsList = collectSubjectsData();
                saveSubjectsToSharedPreferences(subjectsList); // Save to SharedPreferences

                String name = getIntent().getStringExtra("name");

                // Save to Firebase
                saveDataToFirebase(name, subjectsList);

                // Prepare data for StudyScheduleActivity
                ArrayList<String> subjectNames = new ArrayList<>();
                ArrayList<String> subjectGrades = new ArrayList<>();

                for (Map<String, String> subjectData : subjectsList) {
                    subjectNames.add(subjectData.get("subject"));
                    subjectGrades.add(subjectData.get("grade"));
                }

                // Pass subjects and grades to StudyScheduleActivity
                Intent intent = new Intent(SubjectsActivity.this, StudyScheduleActivity.class);
                intent.putStringArrayListExtra("subjects", subjectNames);
                intent.putStringArrayListExtra("grades", subjectGrades);
                startActivity(intent);
                finish();
            }
        });


    }
}
