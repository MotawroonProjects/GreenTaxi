package com.app.green_taxi.services;

import com.app.green_taxi.models.CurrentOrderDataModel;
import com.app.green_taxi.models.OrderDataModel;
import com.app.green_taxi.models.StatusResponse;
import com.app.green_taxi.models.UserModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {
    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/update_location")
    Call<StatusResponse> updateLocation(@Field("user_id") int user_id,
                                        @Field("latitude") double latitude,
                                        @Field("longitude") double longitude
    );

    @FormUrlEncoded
    @POST("api/update-firebase")
    Call<StatusResponse> updateFirebaseToken(@Field("phone_token") String phone_token,
                                             @Field("user_id") int user_id,
                                             @Field("software_type") String software_type

    );

    @FormUrlEncoded
    @POST("api/logout")
    Call<StatusResponse> logout(@Field("phone_token") String phone_token,
                                @Field("user_id") int user_id,
                                @Field("software_type") String software_type

    );

    @GET("api/new_orders")
    Call<OrderDataModel> getNewOrders(@Query(value = "driver_id") int driver_id,
                                      @Query(value = "order") String order
    );

    @GET("api/my_orders")
    Call<CurrentOrderDataModel> getCurrentPreviousOrders(@Query(value = "driver_id") int driver_id,
                                                         @Query(value = "status") String status,
                                                         @Query(value = "order") String order
    );


    @FormUrlEncoded
    @POST("api/update-status")
    Call<StatusResponse> updateStatus(@Field("user_id") int user_id,
                                      @Field("is_busy") String is_busy

    );

    @FormUrlEncoded
    @POST("api/update_order_status")
    Call<StatusResponse> acceptRefuseOrder(@Field("driver_id") int driver_id,
                                           @Field("order_id") int order_id,
                                           @Field("status") String status

    );

    @FormUrlEncoded
    @POST("api/finish_order_request")
    Call<StatusResponse> finishOrder(@Field("driver_id") int driver_id,
                                     @Field("order_id") int order_id,
                                     @Field("status") String status

    );

}

