package com.example.seafight;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seafight.userinterface.SigninFragment;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SigninFragment.toMainIfAuthenticated(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }
}