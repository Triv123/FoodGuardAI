//package com.example.foodguardai;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.progressindicator.CircularProgressIndicator;
//import com.example.foodguardai.database.FirebaseManager;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private TextInputEditText etEmail, etPassword;
//    private MaterialButton btnLogin, btnSignUp, btnSkip;
//    private CircularProgressIndicator progressBar;
//    private FirebaseManager firebaseManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        firebaseManager = FirebaseManager.getInstance();
//
//        // Check if user is already logged in
//        if (firebaseManager.isUserLoggedIn()) {
//            navigateToMain();
//            return;
//        }
//
//        setContentView(R.layout.activity_login);
//
//        initViews();
//        setupListeners();
//    }
//
//    private void initViews() {
//        etEmail = findViewById(R.id.etEmail);
//        etPassword = findViewById(R.id.etPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//        btnSignUp = findViewById(R.id.btnSignUp);
//        btnSkip = findViewById(R.id.btnSkip);
//        progressBar = findViewById(R.id.progressBar);
//    }
//
//    private void setupListeners() {
//        btnLogin.setOnClickListener(v -> handleLogin());
//        btnSignUp.setOnClickListener(v -> navigateToSignUp());
//        btnSkip.setOnClickListener(v -> skipLogin());
//    }
//
//    private void handleLogin() {
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        if (!validateInput(email, password)) {
//            return;
//        }
//
//        showLoading(true);
//
//        firebaseManager.signIn(email, password, new FirebaseManager.AuthCallback() {
//            @Override
//            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
//                showLoading(false);
//                Toast.makeText(LoginActivity.this,
//                        "Welcome back, " + user.getEmail(), Toast.LENGTH_SHORT).show();
//                navigateToMain();
//            }
//
//            @Override
//            public void onError(String error) {
//                showLoading(false);
//                Toast.makeText(LoginActivity.this,
//                        "Login failed: " + error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private boolean validateInput(String email, String password) {
//        if (email.isEmpty()) {
//            etEmail.setError("Email required");
//            return false;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            etEmail.setError("Invalid email format");
//            return false;
//        }
//
//        if (password.isEmpty()) {
//            etPassword.setError("Password required");
//            return false;
//        }
//
//        if (password.length() < 6) {
//            etPassword.setError("Password must be at least 6 characters");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void showLoading(boolean show) {
//        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
//        btnLogin.setEnabled(!show);
//        btnSignUp.setEnabled(!show);
//        btnSkip.setEnabled(!show);
//    }
//
//    private void navigateToSignUp() {
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
//    }
//
//    private void skipLogin() {
//        // Allow guest access
//        navigateToMain();
//    }
//
//    private void navigateToMain() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//}

package com.example.foodguardai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.foodguardai.database.FirebaseManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnSignUp, btnSkip;
    private CircularProgressIndicator progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseManager = FirebaseManager.getInstance();

        // Check if user is already logged in
        if (firebaseManager.isUserLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSkip = findViewById(R.id.btnSkip);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        // BUG FIX: Navigate to RegisterActivity (not SignUpActivity - they were duplicates)
        btnSignUp.setOnClickListener(v -> navigateToRegister());
        btnSkip.setOnClickListener(v -> navigateToMain());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) return;

        showLoading(true);

        firebaseManager.signIn(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                showLoading(false);
                String name = user.getEmail() != null
                        ? user.getEmail().split("@")[0] : "User";
                Toast.makeText(LoginActivity.this,
                        "Welcome back, " + name + "!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        // BUG FIX: Clear errors before re-validating
        etEmail.setError(null);
        etPassword.setError(null);

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnSignUp.setEnabled(!show);
        btnSkip.setEnabled(!show);
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}