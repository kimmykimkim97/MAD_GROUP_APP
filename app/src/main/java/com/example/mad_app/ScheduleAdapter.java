package com.example.mad_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<ScheduleItem> scheduleList;
    private final OnDeleteClickListener onDeleteClickListener; // Optional listener for delete button

    // Constructor for cases with delete functionality
    public ScheduleAdapter(List<ScheduleItem> scheduleList, OnDeleteClickListener onDeleteClickListener) {
        this.scheduleList = scheduleList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    // Alternative constructor when no delete functionality is needed
    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this(scheduleList, null);
    }

    // Update schedule list dynamically
    public void updateScheduleList(List<ScheduleItem> newScheduleList) {
        this.scheduleList.clear();
        this.scheduleList.addAll(newScheduleList);
        notifyDataSetChanged();
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

        // Bind data to the view holder
        holder.tvSubjectName.setText(item.getSubject() + " - Grade: " + item.getGrade());
        holder.tvAllocatedTime.setText("Time: " + item.getSchedule());
        holder.checkboxCompleted.setChecked(item.isChecked());

        // Handle checkbox state changes and sync with Firebase
        holder.checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (item != null && item.getSubject() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                if (userId != null) {
                    String path = "users/" + userId + "/schedules/" + item.getSubject();
                    DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference(path);
                    scheduleRef.child("completed").setValue(isChecked); // Update checkbox state in Firebase
                } else {
                    Log.e("ScheduleAdapter", "User ID is null. Cannot update Firebase.");
                }
            } else {
                Log.e("ScheduleAdapter", "Schedule item or subject name is null.");
            }
        });

        // Hide or handle delete button visibility based on listener
        if (onDeleteClickListener == null) {
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    // ViewHolder for RecyclerView
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvAllocatedTime;
        CheckBox checkboxCompleted;
        Button btnDelete; // Optional delete button

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvAllocatedTime = itemView.findViewById(R.id.tv_time_allocation);
            checkboxCompleted = itemView.findViewById(R.id.checkbox_completed);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    // Listener interface for delete button
    public interface OnDeleteClickListener {
        void onDeleteClick(ScheduleItem item);
    }
}
