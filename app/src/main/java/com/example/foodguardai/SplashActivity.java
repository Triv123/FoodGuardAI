package com.example.foodguardai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodguardai.database.FirebaseManager;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ensure Firebase is initialized before any Firebase operations
        FirebaseApp.initializeApp(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseManager.getInstance().isUserLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}