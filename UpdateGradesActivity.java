package com.example.mad_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateGradesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGrades;
    private UpdateGradesAdapter updateGradesAdapter;
    private List<ScheduleItem> scheduleList;
    private DatabaseReference databaseReference;
    private Button btnSave, btnRegenerate;
    private EditText editTextStudyHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grades);

        databaseReference = FirebaseDatabase.getInstance("https://gradesync-790d0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        // Initialize UI components
        recyclerViewGrades = findViewById(R.id.recycler_view_grades);
        btnSave = findViewById(R.id.btn_save);
        btnRegenerate = findViewById(R.id.btn_regenerate);
        editTextStudyHours = findViewById(R.id.editTextStudyHours);

        recyclerViewGrades.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        updateGradesAdapter = new UpdateGradesAdapter(scheduleList);
        recyclerViewGrades.setAdapter(updateGradesAdapter);

        loadGradesFromFirebase();

        btnSave.setOnClickListener(v -> saveGradesToFirebase());
        btnRegenerate.setOnClickListener(v -> regenerateSchedule());
    }

    private void loadGradesFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You must sign in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        databaseReference.child("users").child(userId).child("schedules")
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        processGradeData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("UpdateGradesActivity", "Failed to fetch grades", databaseError.toException());
                    }
                });
    }

    private void processGradeData(DataSnapshot dataSnapshot) {
        scheduleList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String subjectName = snapshot.child("subject").getValue(String.class);
            String grade = snapshot.child("grade").getValue(String.class);
            String timeAllocated = snapshot.child("timeAllocated").getValue(String.class);

            if (subjectName != null && grade != null && timeAllocated != null) {
                scheduleList.add(new ScheduleItem(subjectName, grade, timeAllocated, 0.0));
            }
        }
        updateGradesAdapter.notifyDataSetChanged();
    }

    private void saveGradesToFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference subjectsRef = databaseReference.child("users").child(userId).child("subjects");

        for (int i = 0; i < scheduleList.size(); i++) {
            ScheduleItem item = scheduleList.get(i);

            if (item.getGrade().isEmpty()) {
                Toast.makeText(this, "Grade data is incomplete for " + item.getSubject(), Toast.LENGTH_SHORT).show();
                continue;
            }

            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subject", item.getSubject());
            subjectData.put("grade", item.getGrade());
            subjectData.put("timeAllocated", item.getTimeAllocated());

            subjectsRef.child("schedule" + i).setValue(subjectData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(UpdateGradesActivity.this, "Grades updated successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(UpdateGradesActivity.this, "Failed to save grades.", Toast.LENGTH_SHORT).show());
        }
    }


    private void regenerateSchedule() {
        String studyHoursText = editTextStudyHours.getText().toString().trim();

        // Ensure user provides input for total study hours
        if (studyHoursText.isEmpty()) {
            Toast.makeText(this, "Please enter total study hours", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalStudyHours = Integer.parseInt(studyHoursText);

        // Grade weights definition
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

        // Allocate time proportionally to study hours based on grade weight
        for (ScheduleItem item : scheduleList) {
            int weight = gradeWeights.get(item.getGrade());
            double subjectStudyTime = ((double) weight / totalWeight) * totalStudyHours;
            item.setTimeAllocated(subjectStudyTime);
        }

        updateGradesAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Schedule regenerated successfully!", Toast.LENGTH_SHORT).show();
    }
}
