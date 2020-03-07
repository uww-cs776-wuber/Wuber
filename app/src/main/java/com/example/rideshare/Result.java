package com.example.rideshare;

public class Result {
    private String userType;
    private String email;
    private String gpsCordinates;
    private String pickuptime;
    public String getEmail() {
        return email;
    }
    private String destination;
    public String getDestination() {
        return destination;
    }
    public String getGpsCordinates() {return gpsCordinates; }
    public String getPickupTime() {
        return pickuptime;
    }
    public String getUserType() { return userType; }
}
