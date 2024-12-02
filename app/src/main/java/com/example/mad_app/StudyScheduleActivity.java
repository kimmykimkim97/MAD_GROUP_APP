package com.example.mad_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private FirebaseFirestore db;
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

        db = FirebaseFirestore.getInstance();
        scheduleList = new ArrayList<>();

        // Get data from previous activity
        ArrayList<String> subjects = getIntent().getStringArrayListExtra("subjects");
        ArrayList<String> grades = getIntent().getStringArrayListExtra("grades");

        // Populate schedule list
        for (int i = 0; i < subjects.size(); i++) {
            scheduleList.add(new ScheduleItem(subjects.get(i), grades.get(i), "Not allocated", 0.0));

        }

        // Set up RecyclerView
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleAdapter);

        // Button to allocate study time
        btnAllocateTime.setOnClickListener(v -> allocateStudyTime());

        // Button to save schedule
        btnSaveSchedule.setOnClickListener(v -> saveSchedulesToFirebase());
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
        List<Map<String, String>> scheduleData = new ArrayList<>();
        for (ScheduleItem item : scheduleList) {
            Map<String, String> data = new HashMap<>();
            data.put("subject", item.getSubject());
            data.put("grade", item.getGrade());
            data.put("studyTime", item.getSchedule());
            scheduleData.add(data);
        }

        db.collection("schedules")
                .add(scheduleData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(StudyScheduleActivity.this, "Schedule saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyScheduleActivity.this, "Error saving schedule: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
