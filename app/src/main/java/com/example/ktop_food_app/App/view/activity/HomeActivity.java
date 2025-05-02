package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.data.remote.FirebaseFoodData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.App.model.repository.CategoryRepository;
import com.example.ktop_food_app.App.model.repository.FoodRepository;
import com.example.ktop_food_app.App.view.activity.Auth.LoginActivity;
import com.example.ktop_food_app.App.view.adapter.CategoryAdapter;
import com.example.ktop_food_app.App.view.adapter.FoodAdapter;
import com.example.ktop_food_app.App.viewmodel.CategoryViewModel;
import com.example.ktop_food_app.App.viewmodel.FoodViewModel;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityNavHomeBinding;
import com.example.ktop_food_app.databinding.NavHeaderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Food> allFoods = new ArrayList<>();
    private ActivityNavHomeBinding binding;
    private NavHeaderBinding headerBinding;
    private FoodRepository foodRepository;
    private CategoryRepository categoryRepository;
    private FoodAdapter foodAdapter;
    private CategoryAdapter categoryAdapter;
    private AuthRepository authRepository;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private CategoryViewModel categoryViewModel;
    private FoodViewModel foodViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0));

        // Khởi tạo AuthRepository
        authRepository = new AuthRepository(new FirebaseAuthData());
        mDatabase = authRepository.getDatabaseReference();

        // Kiểm tra người dùng hiện tại
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        currentUserId = currentUser.getUid();

        foodRepository = new FoodRepository();
        categoryRepository = new CategoryRepository();

        setupRecyclerViews();
        setupViewModels();
        handleSearchButton();
        loadData();
        handleMenuButton();
        handleNavigationMenu();
        handleBackUpButton();
        handleLogoutButton();
        handleProfileImage();
        handleCartButton();
        loadProfileImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại ảnh đại diện mỗi khi activity được quay lại
        loadProfileImage();
    }

    // Cập nhật hàm loadProfileImage để lấy chuỗi Base64 từ Realtime Database và hiển thị
    private void loadProfileImage() {
        DatabaseReference userRef = mDatabase.child("users").child(currentUserId).child("profile").child("avatar");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String avatarBase64 = dataSnapshot.getValue(String.class);
                if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        binding.home.imgUser.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        binding.home.imgUser.setImageResource(R.drawable.img_user);
                        Toast.makeText(HomeActivity.this, "Lỗi hiển thị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.home.imgUser.setImageResource(R.drawable.img_user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Lỗi tải avatar: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                binding.home.imgUser.setImageResource(R.drawable.img_user);
            }
        });
    }

    private void handleBackUpButton() {
        headerBinding.imgBackArrow.setOnClickListener(v ->
                binding.drawerLayout.closeDrawer(GravityCompat.START));
    }

    private void handleLogoutButton() {
        binding.logoutButton.setOnClickListener(v -> {
            authRepository.getCurrentUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void handleMenuButton() {
        binding.home.menuButtonIcon.setOnClickListener(v ->
                binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    private void handleNavigationMenu() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_track_order) {
                Intent intent = new Intent(this, TrackOrderActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_order_history) {
                Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void handleProfileImage() {
        binding.home.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent); // Sử dụng startActivity
            }
        });
    }

    private void handleCartButton() {
        binding.home.cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerViews() {
        // GridView cho Categories
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>());
        binding.home.gridViewCategories.setAdapter(categoryAdapter);

        // RecyclerView cho Foods
        binding.home.recyclerViewFoods.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        foodAdapter = new FoodAdapter(this, new ArrayList<>(), null);
        binding.home.recyclerViewFoods.setAdapter(foodAdapter);
    }

    private void setupViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);

        categoryViewModel.getCategoriesLiveData().observe(this, categoryList -> {
            categoryAdapter.setCategoryList(categoryList);
        });

        categoryViewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, "Lỗi tải danh mục: " + error, Toast.LENGTH_SHORT).show();
        });

        foodViewModel.getFoodListLiveData().observe(this, foodList -> {
            foodAdapter.setFoodList(foodList);
        });

        foodViewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, "Lỗi tải món ăn: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void handleSearchButton() {
        binding.home.searchButtonIcon.setOnClickListener(v -> {
            String query = binding.home.edtSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lọc danh sách món ăn
            List<Food> filteredList = new ArrayList<>();
            String normalizedQuery = handleRemoveDiacritics(query.toLowerCase());
            for (Food food : allFoods) {
                String normalizedTitle = handleRemoveDiacritics(food.getTitle().toLowerCase());
                if (food.isBestFood() && normalizedTitle.contains(normalizedQuery)) {
                    filteredList.add(food);
                }
            }
            
            if (filteredList.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Không tìm thấy món ăn nào", Toast.LENGTH_SHORT).show();
                return;
            }

            // Truyền danh sách sang SearchActivity
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            intent.putExtra("filtered_list", new ArrayList<>(filteredList));
            intent.putExtra("search_query", query);
            startActivity(intent);
        });
    }

    private String handleRemoveDiacritics(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        text = text.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        text = text.replaceAll("[ìíịỉĩ]", "i");
        text = text.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        text = text.replaceAll("[ùúụủũưừứựửữ]", "u");
        text = text.replaceAll("[ỳýỷỹỵ]", "y");
        text = text.replaceAll("đ", "d");
        return text;
    }

    private void loadData() {
        foodRepository.getFoodList(new FirebaseFoodData.FoodCallback() {
            @Override
            public void onSuccess(List<Food> foodList) {
                allFoods.clear();
                for (Food food : foodList) {
                    if (food.isBestFood()) {
                        allFoods.add(food);
                    }
                }
                foodAdapter.setFoodList(allFoods);
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}