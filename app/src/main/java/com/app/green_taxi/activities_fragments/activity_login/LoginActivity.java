package com.app.green_taxi.activities_fragments.activity_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.app.green_taxi.databinding.ActivityLoginBinding;
import com.app.green_taxi.language.Language;
import com.app.green_taxi.models.LoginModel;
import com.app.green_taxi.preferences.Preferences;
import com.app.green_taxi.share.Common;
import com.google.android.gms.maps.SupportMapFragment;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String lang;
    private LoginModel loginModel;
    private Preferences preferences;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        initView();
    }

    private void initView() {
        preferences=Preferences.getInstance();
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        loginModel = new LoginModel();
        binding.setModel(loginModel);
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith("0")){
                    binding.edtPhone.setText(null);
                }
            }
        });
        binding.btnLogin.setOnClickListener(view -> {
            if (loginModel.isDataValid(this)) {
                Common.CloseKeyBoard(this, binding.edtPhone);
                navigateToVerificationCode();
            }
        });





    }

    private void navigateToVerificationCode() {
        Intent intent = new Intent(LoginActivity.this, VerificationCodeActivity.class);
        intent.putExtra("phone_code", loginModel.getPhone_code());
        intent.putExtra("phone", loginModel.getPhone());
        startActivity(intent);
        finish();
    }


}