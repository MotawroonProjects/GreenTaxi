package com.app.green_taxi.activities_fragments.activity_splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.HomeActivity;
import com.app.green_taxi.activities_fragments.activity_login.LoginActivity;
import com.app.green_taxi.databinding.ActivitySplashBinding;
import com.app.green_taxi.language.Language;
import com.app.green_taxi.models.UserModel;
import com.app.green_taxi.preferences.Preferences;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private UserModel userModel;
    private Preferences preferences;
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        initView();
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        new Handler().postDelayed(() -> {
            if (userModel==null){
                navigateToLoginActivity();
            }else {

                navigateToHomeActivity();
            }
        }, 3000);

    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}