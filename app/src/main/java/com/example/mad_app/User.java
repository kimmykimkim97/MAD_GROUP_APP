package com.example.mad_app;

import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private int numberOfSubjects;
    private List<Map<String, String>> subjects; // List of subjects with grades
    private List<Map<String, Object>> studySchedule; // List for storing allocated study time per subject

    // Default constructor (required for Firebase Realtime Database)
    public User() {
    }

    // Constructor for initializing name and number of subjects
    public User(String name, int numberOfSubjects) {
        this.name = name;
        this.numberOfSubjects = numberOfSubjects;
    }

    // Constructor for initializing all fields
    public User(String name, int numberOfSubjects, List<Map<String, String>> subjects, List<Map<String, Object>> studySchedule) {
        this.name = name;
        this.numberOfSubjects = numberOfSubjects;
        this.subjects = subjects;
        this.studySchedule = studySchedule;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for numberOfSubjects
    public int getNumberOfSubjects() {
        return numberOfSubjects;
    }

    public void setNumberOfSubjects(int numberOfSubjects) {
        this.numberOfSubjects = numberOfSubjects;
    }

    // Getter and setter for subjects
    public List<Map<String, String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Map<String, String>> subjects) {
        this.subjects = subjects;
    }

    // Getter and setter for studySchedule
    public List<Map<String, Object>> getStudySchedule() {
        return studySchedule;
    }

    public void setStudySchedule(List<Map<String, Object>> studySchedule) {
        this.studySchedule = studySchedule;
    }

    // toString() method for debugging
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", numberOfSubjects=" + numberOfSubjects +
                ", subjects=" + subjects +
                ", studySchedule=" + studySchedule +
                '}';
    }
}
