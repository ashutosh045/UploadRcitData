package com.traceme.uploadrcitdata;

public class studDetails {
    String name, branch, course;

    public studDetails(String branch, String course, String name) {
        this.name = name;
        this.branch = branch;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
