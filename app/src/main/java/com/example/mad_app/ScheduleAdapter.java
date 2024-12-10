package com.example.mad_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateScheduleList(List<ScheduleItem> newScheduleList) {
        this.scheduleList = newScheduleList;
        notifyDataSetChanged();  // Notify the adapter to refresh the view with the new data
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);

        // Set subject name and grade
        holder.tvSubjectName.setText(item.getSubject() + " - Grade: " + item.getGrade());

        // Set allocated time
        holder.tvAllocatedTime.setText("Time: " + item.getSchedule());

        // Set checkbox state based on the isChecked value
        holder.checkboxCompleted.setChecked(item.isChecked());

        // Set listener for checkbox change
        holder.checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && item != null && item.getSubject() != null) {
                String userId = user.getUid(); // Get current user's ID

                // Construct Firebase path using subject and a unique identifier (like a date or time)
                String path = "users/" + userId + "/schedules/" + item.getGrade() + "/" + item.getSubject();

                DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference(path);
                scheduleRef.child("completed").setValue(isChecked);  // Update checkbox state in Firebase
            } else {
                Log.e("ScheduleAdapter", "User ID or Subject Name is null. Cannot update Firebase.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvAllocatedTime;
        CheckBox checkboxCompleted;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvAllocatedTime = itemView.findViewById(R.id.tv_time_allocation);
            checkboxCompleted = itemView.findViewById(R.id.checkbox_completed);
        }
    }
}
