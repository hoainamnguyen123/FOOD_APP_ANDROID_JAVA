package com.example.ktop_food_app.App.view.activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.App.utils.AuthValidator;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private boolean isConfirmPasswordVisible = false;
    private ActivitySignUpBinding binding;
    private boolean isPasswordVisible = false;
    private AuthRepository authRepository;
    private AuthValidator.OnValidationError validationErrorCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(new FirebaseAuthData());
        validationErrorCallback = message -> binding.edtUserName.setError(message);

        checkActive();
        handleLogin();
        handleSignup();
        handleVisibilityToggle();
        handleTextWatchers();
        handleCheckboxChange();
    }

    private void checkActive() {
        if (validateSignUp() && binding.checkBox.isChecked()) {
            binding.btnSignup.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnSignup.setEnabled(true);
        } else {
            binding.btnSignup.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnSignup.setEnabled(false);
        }
    }

    private void handleCheckboxChange() {
        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkActive());
    }

    private void handleSignup() {
        binding.btnSignup.setOnClickListener(v -> {
            checkActive();
            if (validateSignUp()) {
                String email = binding.edtUserName.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                authRepository.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = authRepository.getCurrentUser().getUid();
                                authRepository.saveUserToDatabase(userId, email)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("SignUpActivity", "User data saved successfully");
                                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SignUpActivity", "Failed to save user data: " + e.getMessage(), e);
                                            Toast.makeText(SignUpActivity.this, "Lỗi lưu thông tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Đăng ký không thành công: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void handleLogin() {
        binding.txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleVisibilityToggle() {
        binding.visibilityOffIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.visibilityOffIcon.setImageResource(R.drawable.visibility_off);
            } else {
                binding.edtPassword.setTransformationMethod(null);
                binding.visibilityOffIcon.setImageResource(R.drawable.visibility_on);
            }
            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        binding.confirmVisibilityOffIcon.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                binding.edtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.confirmVisibilityOffIcon.setImageResource(R.drawable.visibility_off);
            } else {
                binding.edtConfirmPassword.setTransformationMethod(null);
                binding.confirmVisibilityOffIcon.setImageResource(R.drawable.visibility_on);
            }
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.getText().length());
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });
    }

    private boolean validateSignUp() {
        String email = binding.edtUserName.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();

        return AuthValidator.validateEmail(email, message -> binding.edtUserName.setError(message)) &&
                AuthValidator.validatePassword(password, message -> binding.edtPassword.setError(message)) &&
                AuthValidator.validateConfirmPassword(password, confirmPassword, message -> binding.edtConfirmPassword.setError(message));
    }

    private void handleTextWatchers() {
        binding.edtUserName.addTextChangedListener(new TextWatcher() {
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

        binding.edtPassword.addTextChangedListener(new TextWatcher() {
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

        binding.edtConfirmPassword.addTextChangedListener(new TextWatcher() {
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
