package com.example.rideshare;

public class Result {
    private String userType;
    private String driver;
    private String driverLocation;
    private String email;
    private String gpsCordinates;
    private String pickuptime;
    private String arrived;
    private String passengerRequestStatus;
    private String wheelChair, uwwStudent, elderly, intoxicated;
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
    public String getDriver() { return driver; }
    public String getDriverLocation() { return driverLocation; }
    public String getArrived() { return arrived; }
    public String getPassengerRequestStatus() {
        return passengerRequestStatus;
    }

    public String getWheelChair() {
        return wheelChair;
    }

    public String getUwwStudent() {
        return uwwStudent;
    }

    public String getElderly() {
        return elderly;
    }

    public String getIntoxicated() {
        return intoxicated;
    }
}
