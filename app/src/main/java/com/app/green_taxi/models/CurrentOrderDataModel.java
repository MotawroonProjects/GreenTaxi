package com.app.green_taxi.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CurrentOrderDataModel extends StatusResponse implements Serializable {
    private List<CurrentOrderModel> data;

    public List<CurrentOrderModel> getData() {
        return data;
    }

}
