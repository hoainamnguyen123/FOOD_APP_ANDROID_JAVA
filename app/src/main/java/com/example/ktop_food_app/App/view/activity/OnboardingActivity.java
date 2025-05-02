package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app.App.view.activity.Auth.LoginActivity;
import com.example.ktop_food_app.App.view.activity.Auth.SignUpActivity;
import com.example.ktop_food_app.databinding.ActivityOnboardingBinding;
import com.google.firebase.auth.FirebaseAuth;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra nếu người dùng đã đăng nhập
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(OnboardingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#F9C77A"));
    }

    private void setVariable() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }
}