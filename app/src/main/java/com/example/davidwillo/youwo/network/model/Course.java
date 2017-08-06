package com.example.davidwillo.youwo.network.model;

import com.google.gson.annotations.SerializedName;

public class Course {
    private int courseId;
    private String username;

    @SerializedName("coursename")
    private String name;
    private String classroom;
    private String time;
    private int length;
    private int myindex;
    private String period;
    private int place;

    public Course(String name, String classroom, String time, String period, int length, int myindex, int place) {
        this.name = name;
        this.classroom = classroom;
        this.time = time;
        this.period = period;
        this.length = length;
        this.myindex = myindex;
        this.place = place;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getTime() {
        return time;
    }

    public String getPeriod() {
        return period;
    }

    public int getLength() {
        return length;
    }

    public int getMyindex() {
        return myindex;
    }

    public int getPlace() {
        return place;
    }

    ;

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setMyindex(int myindex) {
        this.myindex = myindex;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
