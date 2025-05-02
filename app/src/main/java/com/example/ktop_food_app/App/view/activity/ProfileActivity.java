package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private AuthRepository authRepository;
    private DatabaseReference mDatabase;
    private String currentUserId;

    // Lưu trữ dữ liệu ban đầu từ cơ sở dữ liệu
    private String initialUsername;
    private String initialPhone;
    private String initialAddress;

    // Lưu trữ chuỗi Base64 của ảnh mới (nếu có)
    private String newAvatarBase64;

    // ActivityResultLauncher để chọn ảnh
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Hiển thị ảnh đã chọn lên imgUser
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .placeholder(R.drawable.img_user)
                            .error(R.drawable.img_user)
                            .into(binding.imgUser);
                    // Chuyển ảnh thành Base64 và lưu tạm
                    convertImageToBase64(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo AuthRepository
        authRepository = new AuthRepository(new FirebaseAuthData());
        mDatabase = authRepository.getDatabaseReference();

        // Kiểm tra người dùng hiện tại
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = currentUser.getUid();

        // Đặt edtEmail thành chỉ đọc
        binding.edtEmail.setEnabled(false);

        // Xử lý sự kiện nhấn vào ảnh đại diện
        binding.imgUser.setOnClickListener(v -> pickImage());

        // Các phương thức khởi tạo
        loadUserData();
        setEditProfileButtonDefault();
        handleTextWatchers();
        handleBackIcon();
        handleEditProfileButton();
    }

    // Mở trình chọn ảnh
    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    // Chuyển ảnh thành Base64 và lưu tạm vào biến newAvatarBase64
    private void convertImageToBase64(Uri imageUri) {
        try {
            // Đọc ảnh từ URI thành Bitmap
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Nén ảnh để giảm kích thước
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();

            // Chuyển thành chuỗi Base64
            newAvatarBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Kích hoạt nút Edit Profile nếu có thay đổi
            checkActive();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Tải và hiển thị dữ liệu người dùng
    private void loadUserData() {
        DatabaseReference userRef = mDatabase.child("users").child(currentUserId).child("profile");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String displayName = snapshot.child("displayName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String avatarBase64 = snapshot.child("avatar").getValue(String.class);

                    // Lưu trữ dữ liệu ban đầu
                    initialUsername = displayName != null ? displayName : "Chưa cập nhật";
                    initialPhone = phone != null ? phone : "Chưa cập nhật";
                    initialAddress = address != null ? address : "Chưa cập nhật";

                    // Cập nhật UI
                    binding.edtUsername.setText(initialUsername);
                    binding.edtEmail.setText(email != null ? email : "Chưa cập nhật");
                    binding.edtPhone.setText(initialPhone);
                    binding.edtAddress.setText(initialAddress);

                    // Tải ảnh avatar từ Base64
                    if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                        try {
                            byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            binding.imgUser.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            binding.imgUser.setImageResource(R.drawable.img_user);
                            Toast.makeText(ProfileActivity.this, "Lỗi hiển thị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.imgUser.setImageResource(R.drawable.img_user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Đặt trạng thái mặc định cho nút Edit Profile
    private void setEditProfileButtonDefault() {
        binding.btnEditProfile.setBackgroundResource(R.drawable.custom_bg_default);
        binding.btnEditProfile.setEnabled(false);
    }

    // Xử lý nút Back
    private void handleBackIcon() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    // Xử lý nút Edit Profile
    private void handleEditProfileButton() {
        binding.btnEditProfile.setOnClickListener(v -> {
            if (validateEditProfile()) {
                updateUserProfile();
            }
        });
    }

    // Cập nhật thông tin người dùng (bao gồm ảnh nếu có)
    private void updateUserProfile() {
        DatabaseReference userRef = mDatabase.child("users").child(currentUserId).child("profile");

        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", binding.edtUsername.getText().toString().trim());
        updates.put("phone", binding.edtPhone.getText().toString().trim());
        updates.put("address", binding.edtAddress.getText().toString().trim());

        // Nếu có ảnh mới, thêm vào updates
        if (newAvatarBase64 != null) {
            updates.put("avatar", newAvatarBase64);
        }

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật lại dữ liệu ban đầu sau khi lưu thành công
                    initialUsername = binding.edtUsername.getText().toString().trim();
                    initialPhone = binding.edtPhone.getText().toString().trim();
                    initialAddress = binding.edtAddress.getText().toString().trim();

                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    setEditProfileButtonDefault();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    // Kiểm tra và kích hoạt nút Edit Profile
    private void checkActive() {
        // Kiểm tra dữ liệu hợp lệ
        if (!validateEditProfile()) {
            binding.btnEditProfile.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnEditProfile.setEnabled(false);
            return;
        }

        // Kiểm tra xem dữ liệu có thay đổi không
        String currentUsername = binding.edtUsername.getText().toString().trim();
        String currentPhone = binding.edtPhone.getText().toString().trim();
        String currentAddress = binding.edtAddress.getText().toString().trim();

        boolean hasTextChanged = !currentUsername.equals(initialUsername) ||
                !currentPhone.equals(initialPhone) ||
                !currentAddress.equals(initialAddress);

        boolean hasAvatarChanged = newAvatarBase64 != null;

        if (hasTextChanged || hasAvatarChanged) {
            binding.btnEditProfile.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnEditProfile.setEnabled(true);
        } else {
            binding.btnEditProfile.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnEditProfile.setEnabled(false);
        }
    }

    // Xác thực dữ liệu nhập
    private boolean validateEditProfile() {
        String username = binding.edtUsername.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String address = binding.edtAddress.getText().toString().trim();

        // Kiểm tra username
        if (username.isEmpty()) {
            binding.edtUsername.setError("Vui lòng nhập tên");
            return false;
        } else {
            binding.edtUsername.setError(null);
        }

        // Kiểm tra phone
        if (phone.isEmpty()) {
            binding.edtPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        } else {
            binding.edtPhone.setError(null);
        }

        // Kiểm tra address
        if (address.isEmpty()) {
            binding.edtAddress.setError("Vui lòng nhập địa chỉ");
            return false;
        } else {
            binding.edtAddress.setError(null);
        }

        return true;
    }

    // Xử lý sự kiện thay đổi text
    private void handleTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkActive();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.edtUsername.addTextChangedListener(textWatcher);
        binding.edtPhone.addTextChangedListener(textWatcher);
        binding.edtAddress.addTextChangedListener(textWatcher);
    }
}