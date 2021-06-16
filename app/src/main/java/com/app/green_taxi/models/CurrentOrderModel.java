package com.app.green_taxi.models;

import java.io.Serializable;

public class CurrentOrderModel implements Serializable {
    private int id;
    private String status;
    private int user_id;
    private int driver_id;
    private String phone;
    private String date;
    private String time;
    private String address;
    private double latitude;
    private double longitude;
    private String created_at;
    private String updated_at;
    private UserModel.Data user_fk;
    private double distance;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public UserModel.Data getUser_fk() {
        return user_fk;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }
}
