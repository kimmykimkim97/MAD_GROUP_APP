package com.example.mad_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateGradesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGrades;
    private UpdateGradesAdapter updateGradesAdapter;
    private List<ScheduleItem> scheduleList;
    private FirebaseFirestore db;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grades);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        recyclerViewGrades = findViewById(R.id.recycler_view_grades);
        btnSave = findViewById(R.id.btn_save);

        recyclerViewGrades.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        updateGradesAdapter = new UpdateGradesAdapter(scheduleList);
        recyclerViewGrades.setAdapter(updateGradesAdapter);

        // Load grades from Firestore
        loadGradesFromFirebase();

        // Save updated grades to Firestore
        btnSave.setOnClickListener(v -> saveGradesToFirebase());
    }

    private void loadGradesFromFirebase() {
        db.collection("users") // Replace with the correct collection
                .document("user_id") // Replace with the user's document ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        processGradeData(task.getResult());
                    } else {
                        Log.e("UpdateGradesActivity", "Error fetching grades", task.getException());
                        Toast.makeText(this, "Failed to load grades.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processGradeData(DocumentSnapshot documentSnapshot) {
        scheduleList.clear();
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) documentSnapshot.get("subjects");
        if (subjects != null) {
            for (Map<String, Object> subjectData : subjects) {
                String subjectName = subjectData.get("subject") != null ? (String) subjectData.get("subject") : "Unknown Subject";
                String grade = subjectData.get("grade") != null ? (String) subjectData.get("grade") : "N/A";
                Double timeAllocated = subjectData.get("timeAllocated") != null ? (Double) subjectData.get("timeAllocated") : 0.0;

                scheduleList.add(new ScheduleItem(subjectName, grade, timeAllocated + " hours", timeAllocated));
            }
        }
        updateGradesAdapter.notifyDataSetChanged();
    }

    private void saveGradesToFirebase() {
        List<Map<String, Object>> updatedSubjects = new ArrayList<>();
        for (ScheduleItem item : scheduleList) {
            updatedSubjects.add(Map.of(
                    "subject", item.getSubject(),
                    "grade", item.getGrade(),
                    "timeAllocated", item.getTimeAllocated()
            ));
        }

        db.collection("users")
                .document("user_id") // Replace with the user's document ID
                .update("subjects", updatedSubjects)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Grades updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e("UpdateGradesActivity", "Error updating grades", e);
                    Toast.makeText(this, "Failed to update grades.", Toast.LENGTH_SHORT).show();
                });
    }
}
