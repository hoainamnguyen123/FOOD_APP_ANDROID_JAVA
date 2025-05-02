package com.example.ktop_food_app.App.utils;

public class AuthValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Validate email
    public static boolean validateEmail(String email, OnValidationError callback) {
        if (email.isEmpty()) {
            callback.onError("Please enter email");
            return false;
        }
        if (!email.matches(EMAIL_REGEX)) {
            callback.onError("Invalid email format");
            return false;
        }
        return true;
    }

    // Validate password
    public static boolean validatePassword(String password, OnValidationError callback) {
        if (password.isEmpty()) {
            callback.onError("Please enter password");
            return false;
        }
        if (password.length() < 8) {
            callback.onError("Password must be at least 8 characters long");
            return false;
        }
        return true;
    }

    // Validate confirm password
    public static boolean validateConfirmPassword(String password, String confirmPassword, OnValidationError callback) {
        if (confirmPassword.isEmpty()) {
            callback.onError("Please enter confirm password");
            return false;
        }
        if (confirmPassword.length() < 8) {
            callback.onError("Confirm Password must be at least 8 characters long");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            callback.onError("Passwords do not match");
            return false;
        }
        return true;
    }

    // Callback interface để trả về lỗi
    public interface OnValidationError {
        void onError(String message);
    }
}