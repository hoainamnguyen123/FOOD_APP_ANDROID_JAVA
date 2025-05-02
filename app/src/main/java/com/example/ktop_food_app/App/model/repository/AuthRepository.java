package com.example.ktop_food_app.App.model.repository;

import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AuthRepository {
    private final FirebaseAuthData authDataSource;

    // Constructor nhận FirebaseAuthDataSource
    public AuthRepository(FirebaseAuthData authDataSource) {
        this.authDataSource = authDataSource;
    }

    // Kiểm tra user hiện tại
    public FirebaseUser getCurrentUser() {
        return authDataSource.getCurrentUser();
    }

    // Đăng nhập
    public Task signInWithEmailAndPassword(String email, String password) {
        return authDataSource.signInWithEmailAndPassword(email, password);
    }

    // Đăng ký
    public Task createUserWithEmailAndPassword(String email, String password) {
        return authDataSource.createUserWithEmailAndPassword(email, password);
    }

    // Gửi email đặt lại mật khẩu
    public Task sendPasswordResetEmail(String email) {
        return authDataSource.sendPasswordResetEmail(email);
    }

    // Lưu thông tin user vào Realtime Database
    public Task saveUserToDatabase(String userId, String email) {
        return authDataSource.saveUserToDatabase(userId, email);
    }

    // Thêm phương thức để lấy DatabaseReference
    public DatabaseReference getDatabaseReference() {
        return authDataSource.getDatabaseReference();
    }
}