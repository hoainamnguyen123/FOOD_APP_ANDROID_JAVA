package com.example.ktop_food_app.App.view.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.databinding.ActivityFoodDetailBinding;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FoodDetailActivity extends AppCompatActivity {
    private ActivityFoodDetailBinding binding;
    private Food food;
    private int quantity = 1;
    private DatabaseReference cartRef;
    private AuthRepository authRepository;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###", new DecimalFormatSymbols() {{
        setGroupingSeparator('.');
    }});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(new FirebaseAuthData());
        String uid = authRepository.getCurrentUser().getUid();
        cartRef = authRepository.getDatabaseReference().child("users").child(uid).child("cart").child("items");

        getFoodFromIntent();
        setupUI();
        setupListeners();
    }

    private void getFoodFromIntent() {
        food = (Food) getIntent().getSerializableExtra("food");
    }

    private void setupUI() {
        if (food != null) {
            Glide.with(this).load(food.getImagePath()).into(binding.imgFoodDetail);
            binding.txtFoodDetailName.setText(food.getTitle());
            binding.txtFoodDetailPrice.setText(decimalFormat.format(food.getPrice()) + " đ");
            binding.txtFoodDetailDescription.setText(food.getDescription());
            binding.txtFoodDetailRating.setText(String.valueOf(food.getStar()));
            binding.txtFoodDetailTime.setText(food.getTimeValue());
            binding.ratingDetail.setRating(food.getStar());
            updateTotalPrice();
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnIncrease.setOnClickListener(v -> increaseQuantity());
        binding.btnDecrease.setOnClickListener(v -> decreaseQuantity());
        binding.addToCart.setOnClickListener(v -> addToCart());
    }

    private void increaseQuantity() {
        quantity++;
        binding.txtQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();
    }

    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            binding.txtQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        }
    }

    private void updateTotalPrice() {
        long totalPrice = (long) food.getPrice() * quantity;
        binding.txtTotalPrice.setText(decimalFormat.format(totalPrice) + " đ");
    }

    private void addToCart() {
        cartRef.child(food.getFoodId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CartItem existingItem = task.getResult().getValue(CartItem.class);
                if (existingItem != null) {
                    int newQuantity = existingItem.getQuantity() + quantity;
                    cartRef.child(food.getFoodId()).child("quantity").setValue(newQuantity)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật giỏ hàng thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    CartItem cartItem = new CartItem(food.getTitle(), food.getPrice(), quantity, food.getImagePath());
                    cartItem.setFoodId(food.getFoodId());
                    cartRef.child(food.getFoodId()).setValue(cartItem)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Lỗi khi kiểm tra giỏ hàng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}