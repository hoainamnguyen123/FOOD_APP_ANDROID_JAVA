package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app.App.model.repository.AuthRepository;
import com.example.ktop_food_app.App.view.adapter.CartAdapter;
import com.example.ktop_food_app.App.viewmodel.CartViewModel;
import com.example.ktop_food_app.R;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnTotalPriceChangedListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button placeOrderButton;
    private TextView totalPriceTextView;
    private ImageView btnBack;
    private CartViewModel cartViewModel;
    private AuthRepository authRepository; // Declare as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize UI components
        recyclerView = findViewById(R.id.cart_recycler_view);
        placeOrderButton = findViewById(R.id.place_order_button);// Initialize this
        btnBack = findViewById(R.id.btn_back);

        // Initialize AuthRepository
        authRepository = new AuthRepository(new FirebaseAuthData());
        String userId = authRepository.getCurrentUser().getUid();

        // Initialize ViewModel with userId
        cartViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new CartViewModel(userId);
            }
        }).get(CartViewModel.class);

        // Initialize cart
        cartItems = new ArrayList<>();
        setupRecyclerView();
        observeViewModel();

        // Set up event listeners
        btnBack.setOnClickListener(v -> finish());
        placeOrderButton.setOnClickListener(v -> placeOrder());

        cartAdapter.setOnItemRemovedListener(position -> removeItem(position));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void observeViewModel() {
        cartViewModel.getCartItems().observe(this, items -> {
            cartItems.clear();
            if (items != null) {
                cartItems.addAll(items);
            }
            cartAdapter.notifyDataSetChanged();
        });

        cartViewModel.getTotalPrice().observe(this, this::onTotalPriceChanged);

        cartViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(CartActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeItem(int position) {
        String foodId = cartItems.get(position).getFoodId();
        cartViewModel.removeItem(foodId);
    }

    @Override
    public void onTotalPriceChanged(long totalPrice) {
        if (totalPriceTextView != null) {
            totalPriceTextView.setText(String.format("%,d đ", totalPrice));
        }
    }

    private void placeOrder() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, vui lòng thêm món ăn trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Transfer to PaymentActivity with cart data
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
        intent.putExtra("userId", authRepository.getCurrentUser().getUid());
        startActivity(intent);
    }
}