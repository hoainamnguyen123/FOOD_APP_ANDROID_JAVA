package com.example.ktop_food_app.App.view.activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.App.utils.AuthValidator;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private AuthRepository authRepository;
    private AuthValidator.OnValidationError validationErrorCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo AuthRepository với FirebaseAuthDataSource
        authRepository = new AuthRepository(new FirebaseAuthData());
        validationErrorCallback = message -> binding.txtEmail.setError(message);

        // Lấy email từ Intent (nếu có)
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            binding.txtEmail.setText(email);
        }

        handleBackIcon();
        checkActive();
        handleSendCode();
        handleTextWatchers();
    }

    private void handleBackIcon() {
        binding.btnBackIcon.setOnClickListener(v -> finish());
    }

    private void checkActive() {
        if (validateSendCode()) {
            binding.btnSendCode.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnSendCode.setEnabled(true);
        } else {
            binding.btnSendCode.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnSendCode.setEnabled(false);
        }
    }

    private void handleSendCode() {
        binding.btnSendCode.setOnClickListener(v -> {
            if (validateSendCode()) {
                String email = binding.txtEmail.getText().toString().trim();
                binding.progressBar.setVisibility(android.view.View.VISIBLE);

                authRepository.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            binding.progressBar.setVisibility(android.view.View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra email", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private boolean validateSendCode() {
        String email = binding.txtEmail.getText().toString().trim();
        return AuthValidator.validateEmail(email, validationErrorCallback);
    }

    private void handleTextWatchers() {
        binding.txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkActive();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}