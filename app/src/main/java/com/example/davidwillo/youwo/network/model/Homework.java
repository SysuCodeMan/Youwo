package com.example.davidwillo.youwo.network.model;

public class Homework {
    private String username;
    private String coursename;
    private boolean isFinished;
    private String hwDescription;
    private int homeworkId;

    public Homework(boolean isFinished, String hwDescription) {
        this.isFinished = isFinished;
        this.hwDescription = hwDescription;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public Homework(String username, String coursename, boolean isFinished, String hwDescription) {
        this.username = username;
        this.coursename = coursename;
        this.isFinished = isFinished;
        this.hwDescription = hwDescription;
    }

    public boolean getFinished() {
        return isFinished;
    }

    public String getHwDescription() {
        return hwDescription;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setHwDescription(String hwDescription) {
        this.hwDescription = hwDescription;
    }
}
