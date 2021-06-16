package com.app.green_taxi.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel extends StatusResponse implements Serializable {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public static class Data implements Serializable{
        private int id;
        @SerializedName("status")
        private String order_status;
        private int driver_id;
        private int order_id;
        private String created_at;
        private String updated_at;
        private UserModel.Data driver_fk;
        private OrderModel order_fk;
        private double distance;

        public int getId() {
            return id;
        }

        public String getOrder_status() {
            return order_status;
        }

        public int getDriver_id() {
            return driver_id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public UserModel.Data getDriver_fk() {
            return driver_fk;
        }

        public OrderModel getOrder_fk() {
            return order_fk;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }
}
