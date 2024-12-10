package com.example.mad_app;

public class ScheduleItem {
    private String subject;
    private String grade;
    private String schedule;
    private double timeAllocated;
    private boolean isChecked;

    // Constructor to initialize the object with subject, grade, schedule, and time allocated
    public ScheduleItem(String subject, String grade, String schedule, double timeAllocated) {
        this.subject = subject;
        this.grade = grade;
        this.schedule = schedule;
        this.timeAllocated = timeAllocated;
        this.isChecked = false;  // Default value for isChecked
    }

    // Getters and Setters for all fields
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public double getTimeAllocated() {
        return timeAllocated;
    }

    public void setTimeAllocated(double timeAllocated) {
        this.timeAllocated = timeAllocated;
    }
}
