package com.app.green_taxi.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.app.green_taxi.BR;
import com.app.green_taxi.R;


public class LoginModel extends BaseObservable {
    private String phone_code;
    private String phone;
    public ObservableField<String> error_phone = new ObservableField<>();

    public LoginModel() {
        phone_code = "+966";
        phone = "";
    }

    public boolean isDataValid(Context context) {
        if (!phone.isEmpty()) {
            error_phone.set(null);
            return true;
        } else {
            error_phone.set(context.getString(R.string.field_required));

            return false;
        }
    }


    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }
}
