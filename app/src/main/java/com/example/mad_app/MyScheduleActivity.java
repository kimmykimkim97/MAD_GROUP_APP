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
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        // Set the date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;  // Month is 0-based, so we add 1
            Toast.makeText(MyScheduleActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();

            // Now you can query or display schedules for this date
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
        // Assuming you have a RecyclerView and an Adapter (ScheduleAdapter) already set up
        ScheduleAdapter adapter = (ScheduleAdapter) recyclerViewSchedule.getAdapter();
        if (scheduleList.isEmpty()) {
            // Show a message or placeholder if no schedules are available
            Toast.makeText(MyScheduleActivity.this, "No schedules for this date", Toast.LENGTH_SHORT).show();
        }
        // Update the list of schedules in the adapter
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

}
