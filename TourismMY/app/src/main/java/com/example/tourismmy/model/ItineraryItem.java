package com.example.tourismmy.model;

public class ItineraryItem {
    private String time;
    private String activity;
    private String location;

    public ItineraryItem(String time, String activity, String location) {
        this.time = time;
        this.activity = activity;
        this.location = location;
    }

    public String getTime() { return time; }
    public String getActivity() { return activity; }
    public String getLocation() { return location; }
}