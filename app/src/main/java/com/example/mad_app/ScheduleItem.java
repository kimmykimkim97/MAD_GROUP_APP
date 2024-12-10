package com.example.mad_app;

public class ScheduleItem {
    private String subject;
    private String grade;
    private String schedule;
    private double timeAllocated;
    private String date;
    public ScheduleItem(String subject, String grade, String schedule, double timeAllocated) {
        this.subject = subject;
        this.grade = grade;
        this.schedule = schedule;
        this.timeAllocated = timeAllocated;

    }
    public void ScheduleItem1(String subject, String grade, String schedule, double timeAllocated, String date) {
        this.subject = subject;
        this.grade = grade;
        this.schedule = schedule;
        this.timeAllocated = timeAllocated;
        this.date = date;
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
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
