package com.example.ktop_food_app.App.model.data.remote;

import androidx.annotation.NonNull;

import com.example.ktop_food_app.App.model.data.entity.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseOrderData {
    private final DatabaseReference orderRef;

    public FirebaseOrderData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = database.getReference("orders");
    }

    public void getOrders(OrderCallback callback) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        orderRef.orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orderList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(data.getKey());
                        orderList.add(order);
                    }
                }
                callback.onSuccess(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Interface callback để xử lý dữ liệu trả về
    public interface OrderCallback {
        void onSuccess(List<Order> orderList);
        void onFailure(String errorMessage);
    }
}
