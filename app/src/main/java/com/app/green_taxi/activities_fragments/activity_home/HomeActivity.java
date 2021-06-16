package com.app.green_taxi.activities_fragments.activity_home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Current_Order;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Home;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Previous_Order;
import com.app.green_taxi.activities_fragments.activity_login.LoginActivity;
import com.app.green_taxi.databinding.ActivityHomeBinding;
import com.app.green_taxi.language.Language;
import com.app.green_taxi.location_service.LocationService;
import com.app.green_taxi.models.LocationModel;
import com.app.green_taxi.models.StatusResponse;
import com.app.green_taxi.models.UserModel;
import com.app.green_taxi.preferences.Preferences;
import com.app.green_taxi.remote.Api;
import com.app.green_taxi.share.Common;
import com.app.green_taxi.tags.Tags;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.iid.FirebaseInstanceId;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Current_Order fragment_current_order;
    private Fragment_Previous_Order fragment_previous_order;
    private UserModel userModel;
    private String lang;
    private boolean backPressed= false;
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 22;
    private boolean isFirstTime = true,isLocationSuccess=false;
    public LocationModel locationModel;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);




        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.current:
                    displayFragmentCurrentOrder();
                    break;
                case R.id.previous:
                    displayFragmentPreviousOrder();
                    break;
                default:
                    if (!backPressed){
                        displayFragmentMain();
                    }
                    break;
            }
            return true;
        });

        checkPermission();
        EventBus.getDefault().register(this);
        updateFirebaseToken();
        binding.imageLogout.setOnClickListener(v -> logout());
        binding.vSwitch.setOnClickListener(v -> {
            if (binding.vSwitch.isChecked()){
                updateStatus("yes");
            }else {
                updateStatus("no");

            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, loc_req);
        } else {

            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        }
    }

    public void displayFragmentMain() {
        try {
            if (fragment_home == null) {
                fragment_home = Fragment_Home.newInstance();
            }
            if (fragment_current_order != null && fragment_current_order.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_current_order).commit();
            }
            if (fragment_previous_order != null && fragment_previous_order.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_previous_order).commit();
            }


            if (fragment_home.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_home).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_home, "fragment_home").commit();

            }
        } catch (Exception e) {
        }

    }

    public void displayFragmentCurrentOrder() {

        try {
            if (fragment_current_order == null) {
                fragment_current_order = Fragment_Current_Order.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_previous_order != null && fragment_previous_order.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_previous_order).commit();
            }

            if (fragment_current_order.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_current_order).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_current_order, "fragment_current_order").commit();

            }
        } catch (Exception e) {
        }
    }

    public void displayFragmentPreviousOrder() {

        try {
            if (fragment_previous_order == null) {
                fragment_previous_order = Fragment_Previous_Order.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_current_order != null && fragment_current_order.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_current_order).commit();
            }

            if (fragment_previous_order.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_previous_order).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_previous_order, "fragment_previous_order").commit();

            }
        } catch (Exception e) {
        }
    }





    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }



    @Override
    public void onBackPressed() {
        if (isLocationSuccess){
            backPressed = true;
            binding.bottomNavView.setSelectedItemId(R.id.home);
            backPressed = false;

            if (fragment_home != null && fragment_home.isAdded() && fragment_home.isVisible()) {
                finish();
            } else {
                displayFragmentMain();
            }
        }else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        try {
            Intent intent = new Intent(this,LocationService.class);
            stopService(intent);
        }catch (Exception e){

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, LocationService.class);
                startService(intent);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationListener(LocationModel locationModel){
        this.locationModel = locationModel;
        isLocationSuccess = true;
        if (isFirstTime){
            binding.flLocation.setVisibility(View.GONE);
            displayFragmentMain();
            isFirstTime = false;
        }
    }




    private void updateFirebaseToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    Api.getService(Tags.base_url)
                            .updateFirebaseToken(token, userModel.getData().getId(), "android")
                            .enqueue(new Callback<StatusResponse>() {
                                @Override
                                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {

                                        if (response.body().getStatus() == 200) {
                                            userModel.getData().setFirebaseToken(token);
                                            preferences.create_update_userdata(HomeActivity.this, userModel);
                                            Log.e("token", "updated successfully");

                                        }
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
                } catch (Exception e) {


                }

            }
        });

    }

    public void logout() {

        if (userModel==null){
            finish();
            return;
        }
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .logout(userModel.getData().getFirebase_token(),userModel.getData().getId(),"android")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                navigateToSignInActivity();
                            }

                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }


    private void navigateToSignInActivity() {
        preferences.clear(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateStatus(String status){
        Api.getService(Tags.base_url)
                .updateStatus(userModel.getData().getId(),status)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                            }else {
                                if (status.equals("yes")){
                                    binding.vSwitch.setChecked(false);
                                }else{
                                    binding.vSwitch.setChecked(true);

                                }
                            }

                        } else {
                            if (status.equals("yes")){
                                binding.vSwitch.setChecked(false);
                            }else{
                                binding.vSwitch.setChecked(true);

                            }
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            if (status.equals("yes")){
                                binding.vSwitch.setChecked(false);
                            }else{
                                binding.vSwitch.setChecked(true);

                            }
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }




}