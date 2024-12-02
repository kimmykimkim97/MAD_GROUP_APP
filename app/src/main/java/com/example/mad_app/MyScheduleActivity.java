package com.example.mad_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        recyclerViewSchedule = findViewById(R.id.recycler_view_schedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        // Load the schedule from Firebase
        loadScheduleFromFirebase();
    }

    private void loadScheduleFromFirebase() {
        db.collection("users") // Replace with your Firestore collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        processScheduleData(task.getResult());
                    } else {
                        Log.e("MyScheduleActivity", "Error fetching data", task.getException());
                        Toast.makeText(this, "Failed to load schedule.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processScheduleData(QuerySnapshot querySnapshot) {
        scheduleList.clear(); // Clear old data
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) document.get("subjects");
            if (subjects != null) {
                for (Map<String, Object> subjectData : subjects) {
                    // Safely extract data
                    String subjectName = subjectData.get("subject") != null ? (String) subjectData.get("subject") : "Unknown Subject";
                    String grade = subjectData.get("grade") != null ? (String) subjectData.get("grade") : "N/A";
                    Double timeAllocatedValue = subjectData.get("timeAllocated") != null ? (Double) subjectData.get("timeAllocated") : 0.0;
                    String schedule = timeAllocatedValue + " hours";

                    // Add to the schedule list
                    scheduleList.add(new ScheduleItem(subjectName, grade, schedule, timeAllocatedValue));
                }
            }
        }

        if (scheduleList.isEmpty()) {
            Toast.makeText(this, "No schedule found.", Toast.LENGTH_SHORT).show();
        }

        scheduleAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }
}
