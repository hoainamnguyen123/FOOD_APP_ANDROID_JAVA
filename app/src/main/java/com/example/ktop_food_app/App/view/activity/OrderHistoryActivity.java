package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.view.adapter.OrderAdapter;
import com.example.ktop_food_app.App.viewmodel.OrderViewModel;
import com.example.ktop_food_app.databinding.ActivityOrderHistoryBinding;
import com.example.ktop_food_app.databinding.ItemDialogReviewBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private final List<Order> orderList = new ArrayList<>();
    private ActivityOrderHistoryBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;
    private DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference();

        initRecyclerView();
        initViewModel();
        setupListeners();
    }

    private void initRecyclerView() {
        // Use VIEW_TYPE_ORDER_HISTORY for OrderHistoryActivity
        adapter = new OrderAdapter(this, orderList, this, OrderAdapter.VIEW_TYPE_ORDER_HISTORY);
        binding.recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewOrderHistory.setAdapter(adapter);
    }

    private void initViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderListLiveData().observe(this, orders -> {
            orderList.clear();
            // Filter for completed orders only
            for (Order order : orders) {
                if ("completed".equalsIgnoreCase(order.getStatus()) || "cancelled".equals(order.getStatus())) {
                    orderList.add(order);
                }
            }
            adapter.notifyDataSetChanged();
        });

        orderViewModel.getErrorMassage().observe(this, error -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderHistoryDetailsActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }

    @Override
    public void onReorderClick(Order order) {
        // Check if the order is valid
        if (order == null || order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            Toast.makeText(this, "Đơn hàng không hợp lệ để đặt lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the list of items from the order
        List<CartItem> cartItems = order.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Đơn hàng không có món ăn để đặt lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass the list of items to PaymentActivity
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
        intent.putExtra("userId", order.getUid());
        startActivity(intent);
    }

    @Override
    public void onReviewClick(Order order) {
        // Check if the order is valid
        if (order == null || order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            Toast.makeText(this, "Đơn hàng không hợp lệ để đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create dialog layout using View Binding
        ItemDialogReviewBinding dialogBinding = ItemDialogReviewBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());

        // "Submit" button
        builder.setPositiveButton("Submit", (dialog, which) -> {
            float rating = dialogBinding.ratingBar.getRating();
            String comment = dialogBinding.commentEditText.getText().toString().trim();

            // Validate data
            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create review data
            Map<String, Object> review = new HashMap<>();
            review.put("orderId", order.getOrderId());
            review.put("userId", order.getUid());
            review.put("rating", rating);
            review.put("comment", comment);
            review.put("timestamp", System.currentTimeMillis());

            // Save review to Realtime Database
            String reviewId = db.child("reviews").push().getKey();
            if (reviewId != null) {
                db.child("reviews")
                        .child(reviewId)
                        .setValue(review)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Đánh giá đã được gửi thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi khi gửi đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // "Cancel" button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show dialog
        builder.setCancelable(false);
        builder.create().show();
    }
}