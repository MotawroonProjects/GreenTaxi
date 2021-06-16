package com.app.green_taxi.models;

import java.io.Serializable;

public class UserModel extends StatusResponse implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
       private int id;
       private String code;
       private String user_type;
       private String phone_code;
       private String phone;
       private String name;
       private String email;
       private String address;
       private double latitude;
       private double longitude;
       private String gender;
       private String birthday;
       private String logo;
       private String banner;
       private String approved_status;
       private String approved_by;
       private String is_blocked;
       private String is_login;
       private String is_busy;
       private String logout_time;
       private String email_verified_at;
       private String confirmation_code;
       private String forget_password_code;
       private String software_type;
       private String deleted_at;
       private String firebase_token="";

        public int getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
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

        public String getGender() {
            return gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getLogo() {
            return logo;
        }

        public String getBanner() {
            return banner;
        }

        public String getApproved_status() {
            return approved_status;
        }

        public String getApproved_by() {
            return approved_by;
        }

        public String getIs_blocked() {
            return is_blocked;
        }

        public String getIs_login() {
            return is_login;
        }

        public String getIs_busy() {
            return is_busy;
        }

        public String getLogout_time() {
            return logout_time;
        }

        public String getEmail_verified_at() {
            return email_verified_at;
        }

        public String getConfirmation_code() {
            return confirmation_code;
        }

        public String getForget_password_code() {
            return forget_password_code;
        }

        public String getSoftware_type() {
            return software_type;
        }

        public String getDeleted_at() {
            return deleted_at;
        }


        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setFirebaseToken(String token) {
            this.firebase_token = token;
        }

        public String getFirebase_token() {
            return firebase_token;
        }
    }
}

