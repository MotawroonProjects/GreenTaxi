package com.app.green_taxi.models;

import java.io.Serializable;

public class OrderModel implements Serializable {
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
    private UserModel.Data user_fk;
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

    public UserModel.Data getUser_fk() {
        return user_fk;
    }
}
