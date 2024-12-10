package com.example.mad_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private CalendarView calendarView;
    private DatabaseReference databaseReference; // Reference to Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        // Initialize the CalendarView
        calendarView = findViewById(R.id.calendar_view);
        // Initialize RecyclerView and Adapter
        recyclerViewSchedule = findViewById(R.id.recycler_view_schedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList, new ScheduleAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(ScheduleItem item) {
                deleteScheduleItem(item); // Define this method to handle deletion
            }
        });
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        // Set the date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;  // Month is 0-based, so we add 1
            Toast.makeText(MyScheduleActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
            loadSchedulesForDate(selectedDate);
        });
        // Initialize Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance("https://gradesync-790d0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        // Load the schedule from Realtime Database
        loadScheduleFromFirebase();
    }

    private void loadSchedulesForDate(String date) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference scheduleRef = databaseReference.child("users").child(userId).child("schedules");

            scheduleRef.orderByChild("date").equalTo(date)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<ScheduleItem> scheduleList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ScheduleItem item = snapshot.getValue(ScheduleItem.class);
                                if (item != null) {
                                    scheduleList.add(item);
                                }
                            }
                            updateRecyclerView(scheduleList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MyScheduleActivity.this, "Failed to load schedules.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateRecyclerView(List<ScheduleItem> scheduleList) {
        ScheduleAdapter adapter = (ScheduleAdapter) recyclerViewSchedule.getAdapter();
        if (scheduleList.isEmpty()) {
            Toast.makeText(MyScheduleActivity.this, "No schedules for this date", Toast.LENGTH_SHORT).show();
        }
        adapter.updateScheduleList(scheduleList);
    }

    private void loadScheduleFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "unknown";

        databaseReference.child("users").child(userId).child("schedules")
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        processScheduleData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("MyScheduleActivity", "Failed to fetch data from Realtime Database", databaseError.toException());
                        Toast.makeText(MyScheduleActivity.this, "Failed to load schedule.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processScheduleData(DataSnapshot dataSnapshot) {
        scheduleList.clear(); // Clear old data
        for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
            String subjectName = scheduleSnapshot.child("subject").getValue(String.class);
            String grade = scheduleSnapshot.child("grade").getValue(String.class);
            String timeAllocated = scheduleSnapshot.child("allocatedTime").getValue(String.class);
            Boolean isChecked = scheduleSnapshot.child("isChecked").getValue(Boolean.class);

            if (subjectName != null && grade != null && timeAllocated != null) {
                ScheduleItem item = new ScheduleItem(subjectName, grade, timeAllocated, 0.0);
                item.setChecked(isChecked != null && isChecked); // Default to false if null
                scheduleList.add(item);
            }
        }

        if (scheduleList.isEmpty()) {
            Toast.makeText(this, "No schedules found in database.", Toast.LENGTH_SHORT).show();
        }

        scheduleAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void deleteScheduleItem(ScheduleItem item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null && item != null && item.getSubject() != null) {
            DatabaseReference scheduleRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("schedules")
                    .child(item.getSubject());

            scheduleRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MyScheduleActivity.this, "Schedule deleted successfully.", Toast.LENGTH_SHORT).show();
                    scheduleList.remove(item); // Remove the item locally
                    scheduleAdapter.notifyDataSetChanged(); // Notify the adapter
                } else {
                    Toast.makeText(MyScheduleActivity.this, "Failed to delete schedule.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MyScheduleActivity.this, "Invalid schedule or user ID.", Toast.LENGTH_SHORT).show();
        }
    }

}
