package com.app.green_taxi.models;

import java.io.Serializable;

public class NotFireModel implements Serializable {
    private boolean status;

    public NotFireModel(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
