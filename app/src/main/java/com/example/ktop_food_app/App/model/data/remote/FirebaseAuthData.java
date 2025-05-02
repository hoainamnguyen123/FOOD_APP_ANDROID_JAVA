package com.example.ktop_food_app.App.model.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthData {
    private static final String DATABASE_URL = "https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app";
    private final FirebaseAuth mAuth;

    public FirebaseAuthData() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Kiểm tra user hiện tại
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Đăng nhập
    public Task signInWithEmailAndPassword(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // Đăng ký
    public Task createUserWithEmailAndPassword(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    // Gửi email đặt lại mật khẩu
    public Task sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    // Lưu thông tin user vào Realtime Database
    public Task saveUserToDatabase(String userId, String email) {
        DatabaseReference userRef = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("users")
                .child(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("profile", new HashMap<String, Object>() {{
            String username = email.substring(0, email.indexOf('@'));
            put("displayName", "User_" + username);
            put("email", email);
            put("address", "chưa cập nhật");
            put("avatar", "");
            put("phone", "chưa cập nhật");
        }});
        userData.put("cart", new ArrayList<>());

        return userRef.setValue(userData);
    }

    // Thêm phương thức để lấy DatabaseReference
    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance(DATABASE_URL).getReference();
    }
}