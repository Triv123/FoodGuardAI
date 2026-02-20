package com.example.foodguardai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodguardai.database.FirebaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private MaterialButton btnScanProduct;
    private MaterialCardView cardHistory;
    private MaterialCardView cardAbout;
    private MaterialCardView cardDietDashboard;
    private MaterialButton btnLogout;
    private TextView tvWelcome;
    
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        firebaseManager = FirebaseManager.getInstance();
        
        initViews();
        setupClickListeners();
        updateUI();
    }

    private void initViews() {
        btnScanProduct = findViewById(R.id.btnScanProduct);
        cardHistory = findViewById(R.id.cardHistory);
        cardAbout = findViewById(R.id.cardAbout);
        cardDietDashboard = findViewById(R.id.cardDietDashboard);
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);
    }

    private void updateUI() {
        if (firebaseManager.isUserLoggedIn() && firebaseManager.getCurrentUser() != null) {
            String email = firebaseManager.getCurrentUser().getEmail();
            tvWelcome.setText("Welcome, " + (email != null ? email.split("@")[0] : "User"));
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvWelcome.setText("Welcome, Guest");
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnScanProduct.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openScanner();
            } else {
                requestCameraPermission();
            }
        });

        cardDietDashboard.setOnClickListener(v -> {
            if (firebaseManager.isUserLoggedIn()) {
                Intent intent = new Intent(this, DietDashboardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please login to track your diet", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        cardAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            firebaseManager.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan products",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openScanner() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}