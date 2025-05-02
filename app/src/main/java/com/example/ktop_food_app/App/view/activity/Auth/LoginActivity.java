package com.example.ktop_food_app.App.view.activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.App.utils.AuthValidator;
import com.example.ktop_food_app.App.view.activity.HomeActivity;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;
    private AuthRepository authRepository;
    private AuthValidator.OnValidationError validationErrorCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Khởi tạo AuthRepository với FirebaseAuthDataSource
        authRepository = new AuthRepository(new FirebaseAuthData());

        // Kiểm tra nếu người dùng đã đăng nhập
        if (authRepository.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo callback cho validation
        validationErrorCallback = message -> binding.edtUsername.setError(message);

        checkActive();
        handleLogin();
        handleSignup();
        handleVisibilityToggle();
        handleTextWatchers();
        handleForgotPassword();
    }

    private void checkActive() {
        if (validateLogin()) {
            binding.btnLogin.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnLogin.setEnabled(true);
        } else {
            binding.btnLogin.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnLogin.setEnabled(false);
        }
    }

    private void handleLogin() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtUsername.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (validateLogin()) {
                authRepository.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    binding.edtUsername.setError("Email hoặc mật khẩu không đúng");
                                } catch (FirebaseAuthInvalidUserException e) {
                                    binding.edtUsername.setError("Không tìm thấy tài khoản với email này");
                                } catch (Exception e) {
                                    Toast.makeText(this, "Đăng nhập thất bại: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void handleSignup() {
        binding.txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
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
    }

    private void handleForgotPassword() {
        binding.txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            String email = binding.edtUsername.getText().toString().trim();
            if (!email.isEmpty()) {
                intent.putExtra("email", email);
            }
            startActivity(intent);
        });
    }

    private boolean validateLogin() {
        String email = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        return AuthValidator.validateEmail(email, message -> binding.edtUsername.setError(message)) &&
                AuthValidator.validatePassword(password, message -> binding.edtPassword.setError(message));
    }

    private void handleTextWatchers() {
        binding.edtUsername.addTextChangedListener(new TextWatcher() {
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
    }
}