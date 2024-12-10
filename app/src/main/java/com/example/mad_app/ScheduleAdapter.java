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
        holder.tvSubjectName.setText(item.getSubject() + " - Grade: " + item.getGrade());
        holder.tvAllocatedTime.setText("Time: " + item.getSchedule());
        holder.checkboxCompleted.setChecked(item.isChecked());

        holder.checkboxCompleted.setChecked(item.isChecked());

        holder.checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (item != null && item.getSubject() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
                String path = "users/" + userId + "/schedules/" + item.getSubject();

                // Ensure the path is valid
                if (userId != null && item.getSubject() != null) {
                    DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference(path);
                    scheduleRef.child("completed").setValue(isChecked);  // Update checkbox state in Firebase
                } else {
                    Log.e("ScheduleAdapter", "User ID or Subject Name is null. Cannot update Firebase.");
                }
            } else {
                Log.e("ScheduleAdapter", "Schedule item or subject name is null.");
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
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);  // Make sure ID matches
            tvAllocatedTime = itemView.findViewById(R.id.tv_time_allocation); // Make sure ID matches

            checkboxCompleted = itemView.findViewById(R.id.checkbox_completed);
        }
    }
}
