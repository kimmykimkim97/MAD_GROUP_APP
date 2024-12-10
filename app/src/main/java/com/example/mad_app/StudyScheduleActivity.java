package com.example.mad_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private DatabaseReference databaseReference; // Realtime Database reference
    private EditText editTextStudyHours;
    private Button btnAllocateTime, btnSaveSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);

        recyclerView = findViewById(R.id.recyclerView);
        editTextStudyHours = findViewById(R.id.editText_study_hours);
        btnAllocateTime = findViewById(R.id.btn_allocate_time);
        btnSaveSchedule = findViewById(R.id.btn_save_schedule);

        databaseReference = FirebaseDatabase.getInstance("https://gradesync-790d0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        scheduleList = new ArrayList<>();

        // Get subjects and grades passed from SubjectsActivity
        ArrayList<String> subjects = getIntent().getStringArrayListExtra("subjects");
        ArrayList<String> grades = getIntent().getStringArrayListExtra("grades");

        if (subjects != null && grades != null && subjects.size() == grades.size()) {
            // Populate the schedule list with passed data
            for (int i = 0; i < subjects.size(); i++) {
                scheduleList.add(new ScheduleItem(subjects.get(i), grades.get(i), "Not allocated", 0.0));
            }
        } else {
            Toast.makeText(this, "No subjects/grades data received!", Toast.LENGTH_SHORT).show();
        }

        // Set up RecyclerView
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleAdapter);

        // Button to allocate study time
        btnAllocateTime.setOnClickListener(v -> allocateStudyTime());

        // Button to save schedule
        btnSaveSchedule.setOnClickListener(v -> {
            saveSchedulesToFirebase();
            saveSchedulesToSharedPreferences();
        });
    }

    private void allocateStudyTime() {
        String studyHoursText = editTextStudyHours.getText().toString().trim();
        if (studyHoursText.isEmpty()) {
            Toast.makeText(this, "Please enter total study hours", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalStudyHours = Integer.parseInt(studyHoursText);
        Map<String, Integer> gradeWeights = new HashMap<>();
        gradeWeights.put("A", 1);
        gradeWeights.put("B", 2);
        gradeWeights.put("C", 3);
        gradeWeights.put("D", 4);
        gradeWeights.put("F", 5);

        // Calculate total weight
        int totalWeight = 0;
        for (ScheduleItem item : scheduleList) {
            totalWeight += gradeWeights.get(item.getGrade());
        }

        // Allocate time for each subject
        for (ScheduleItem item : scheduleList) {
            int weight = gradeWeights.get(item.getGrade());
            double subjectStudyTime = ((double) weight / totalWeight) * totalStudyHours;
            item.setSchedule(String.format("%.2f hours", subjectStudyTime));
        }

        scheduleAdapter.notifyDataSetChanged();
    }

    private void saveSchedulesToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You must sign in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = databaseReference.child("users").child(userId).child("schedules");

        // Convert the scheduleList to Firebase-compatible format
        for (ScheduleItem item : scheduleList) {
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("subject", item.getSubject());
            scheduleData.put("grade", item.getGrade());
            scheduleData.put("allocatedTime", item.getSchedule());

            userRef.push().setValue(scheduleData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Schedule saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveSchedulesToSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("UserScheduleData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            JSONArray scheduleArray = new JSONArray();
            for (ScheduleItem item : scheduleList) {
                JSONObject scheduleObject = new JSONObject();
                scheduleObject.put("subject", item.getSubject());
                scheduleObject.put("grade", item.getGrade());
                scheduleObject.put("allocatedTime", item.getSchedule());

                scheduleArray.put(scheduleObject);
            }

            editor.putString("schedules", scheduleArray.toString());
            editor.apply();

            Toast.makeText(this, "Schedule saved locally!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving locally: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
