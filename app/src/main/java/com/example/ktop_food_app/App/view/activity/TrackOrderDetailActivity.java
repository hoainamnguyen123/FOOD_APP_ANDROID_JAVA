package com.example.ktop_food_app.App.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.view.adapter.TrackOrderDetailAdapter;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ActivityTrackOrderDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrackOrderDetailActivity extends AppCompatActivity {

    private ActivityTrackOrderDetailBinding binding;
    private TrackOrderDetailAdapter itemAdapter;
    private Order order;
    private DecimalFormat decimalFormat;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityTrackOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DecimalFormat
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        // Initialize Firebase Database Reference
        ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("orders");

        // Load and process order data
        if (!loadOrderData()) {
            return;
        }

        displayOrderDetails();
        setupRecyclerView();
        setupListeners();
        updateCancelButtonState(order.getStatus());
    }

    // Load order data from Intent
    private boolean loadOrderData() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không thể hiển thị chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    // Display order details on UI
    private void displayOrderDetails() {
        binding.txtOrderId.setText(order.getOrderId());
        binding.txtOrderStatus.setText(order.getStatus());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
        String orderTime;
        try {
            orderTime = dateFormat.format(new Date(order.getCreatedAt()));
        } catch (Exception e) {
            Log.e("TrackOrderDetail", "Error formatting date: " + e.getMessage());
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
        binding.txtTotalPriceOfItem.setText(decimalFormat.format(totalPriceOfItems) + " đ");
        binding.txtDiscount.setText(decimalFormat.format(order.getDiscount()) + " đ");
        binding.txtTotalPayment.setText(decimalFormat.format(order.getTotalPrice()) + " đ");
    }

    // Setup RecyclerView for order items
    private void setupRecyclerView() {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            itemAdapter = new TrackOrderDetailAdapter(this, order.getItems());
            binding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewItems.setAdapter(itemAdapter);
        } else {
            Log.w("TrackOrderDetail", "Order has no items to display");
            Toast.makeText(this, "Đơn hàng không có sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }

    // Update cancel/received button states based on order status
    private void updateCancelButtonState(String status) {
        if ("pending".equalsIgnoreCase(status)) {
            binding.cancelOrderButton.setEnabled(true);
            binding.cancelOrderButton.setBackgroundResource(R.drawable.custom_bg_success);
            binding.orderReceivedButton.setEnabled(false);
            binding.orderReceivedButton.setBackgroundResource(R.drawable.custom_bg_default);
        } else if ("shipping".equalsIgnoreCase(status)) {
            binding.cancelOrderButton.setEnabled(false);
            binding.cancelOrderButton.setBackgroundResource(R.drawable.custom_bg_default);
            binding.orderReceivedButton.setEnabled(true);
            binding.orderReceivedButton.setBackgroundResource(R.drawable.custom_bg_success);
        }
    }

    // Setup button listeners
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Confirm order received
        binding.orderReceivedButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đơn hàng")
                    .setMessage("Bạn đã nhận được đơn hàng chưa?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        ordersRef.child(order.getOrderId()).child("status").setValue("completed")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Đơn hàng đã được đánh dấu là nhận thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Cancel order
        binding.cancelOrderButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hủy đơn hàng")
                    .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                    .setPositiveButton("Hủy đơn hàng", (dialog, which) -> {
                        ordersRef.child(order.getOrderId()).child("status").setValue("cancelled")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Đơn hàng đã được hủy.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }
}