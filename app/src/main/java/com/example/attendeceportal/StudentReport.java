package com.example.attendeceportal;

public class StudentReport {
    private String name;
    private int present;
    private int total;

    public StudentReport(String name, int present, int total) {
        this.name = name;
        this.present = present;
        this.total = total;
    }

    public String getName() { return name; }
    public int getPresent() { return present; }
    public int getTotal() { return total; }
}

