package com.app.green_taxi.location_service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.green_taxi.R;
import com.app.green_taxi.models.LocationModel;
import com.app.green_taxi.models.StatusResponse;
import com.app.green_taxi.models.UserModel;
import com.app.green_taxi.preferences.Preferences;
import com.app.green_taxi.remote.Api;
import com.app.green_taxi.tags.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Preferences preferences;
    private UserModel userModel;


    @Override
    public void onCreate() {
        super.onCreate();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2*60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(false);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {

            Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e("not available", "not available");
                    break;
            }
        });

    }
    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {

        LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
        EventBus.getDefault().post(locationModel);

        Api.getService(Tags.base_url)
                .updateLocation(userModel.getData().getId(),location.getLatitude(),location.getLongitude())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus()==200 ) {
                            userModel.getData().setLatitude(location.getLatitude());
                            userModel.getData().setLongitude(location.getLongitude());
                            preferences.create_update_userdata(LocationService.this,userModel);

                        } else {
                            try {

                                Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("errorToken2", t.getMessage());

                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
