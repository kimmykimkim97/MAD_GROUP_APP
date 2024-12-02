package com.example.mad_app;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpdateGradesAdapter extends RecyclerView.Adapter<UpdateGradesAdapter.GradeViewHolder> {

    private List<ScheduleItem> scheduleList;

    public UpdateGradesAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update_grade, parent, false); // Create a layout for updating grades
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.textSubject.setText(item.getSubject());
        holder.editGrade.setText(item.getGrade());

        // Update the grade in the ScheduleItem when the user modifies it
        holder.editGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                item.setGrade(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView textSubject;
        EditText editGrade;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubject = itemView.findViewById(R.id.text_subject); // Subject TextView
            editGrade = itemView.findViewById(R.id.edit_grade); // Grade EditText
        }
    }
}
