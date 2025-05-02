package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.view.adapter.OrderHistoryDetailsAdapter;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityOrderHistoryDetailsBinding;
import com.example.ktop_food_app.databinding.ItemDialogReviewBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryDetailsActivity extends AppCompatActivity {

    private ActivityOrderHistoryDetailsBinding binding;
    private OrderHistoryDetailsAdapter itemAdapter;
    private Order order;
    private DecimalFormat decimalFormat;
    private DatabaseReference db;  // Added for Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityOrderHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        // Call methods
        if (!loadOrderData()) {
            return;
        }
        displayOrderDetails();
        setupRecyclerView();
        setupListeners();
    }

    // Load order data from Intent
    private boolean loadOrderData() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Log.e("OrderHistoryDetail", "Order is null, cannot display details");
            Toast.makeText(this, "Không thể hiển thị chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    // Display order details
    private void displayOrderDetails() {
        binding.txtOrderId.setText(order.getOrderId());
        binding.txtOrderStatus.setText(order.getStatus());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
        String orderTime;
        try {
            orderTime = dateFormat.format(new Date(order.getCreatedAt()));
        } catch (Exception e) {
            Log.e("OrderHistoryDetail", "Error formatting date: " + e.getMessage());
            orderTime = "N/A";
        }
        binding.txtOrderTime.setText(orderTime);

        String paymentMethod = order.getPaymentMethod();
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            binding.paymentMethod.setText("Cash on Delivery");
            binding.paymentIcon.setImageResource(R.drawable.ic_cod);
        } else if ("Banking(Zalo Pay)".equalsIgnoreCase(paymentMethod)) {
            binding.paymentMethod.setText(paymentMethod);
            binding.paymentIcon.setImageResource(R.drawable.ic_bank);
        } else {
            binding.paymentMethod.setText(paymentMethod);
            binding.paymentIcon.setVisibility(View.GONE);
        }

        double totalPriceOfItems = order.getTotalPrice() + order.getDiscount();
        binding.txtTotalAmount.setText(decimalFormat.format(totalPriceOfItems) + " đ");
        binding.txtTotalPriceItemsAmount.setText(decimalFormat.format(totalPriceOfItems) + " đ");
        binding.txtDiscountAmount.setText(decimalFormat.format(order.getDiscount()) + " đ");
        binding.txtTotalPaymentDetails.setText(decimalFormat.format(order.getTotalPrice()) + " đ");
    }

    // Setup RecyclerView
    private void setupRecyclerView() {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            itemAdapter = new OrderHistoryDetailsAdapter(this, order.getItems());
            binding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewItems.setAdapter(itemAdapter);
        } else {
            Log.w("OrderHistoryDetail", "Order has no items to display");
            Toast.makeText(this, "Đơn hàng không có sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }

    // Setup button listeners
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Review button listener
        binding.btnReviews.setOnClickListener(v -> onReviewClick(order));

        // Reorder button listener
        binding.btnReorder.setOnClickListener(v -> onReorderClick(order));
    }

    // Handle Reorder button click
    private void onReorderClick(Order order) {
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

    // Handle Review button click
    private void onReviewClick(Order order) {
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