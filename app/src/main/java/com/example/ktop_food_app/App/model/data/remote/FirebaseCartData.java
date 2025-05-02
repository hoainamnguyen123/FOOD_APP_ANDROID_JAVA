package com.example.ktop_food_app.App.model.data.remote;

import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCartData {
    private final DatabaseReference cartRef;

    public FirebaseCartData(String userId) {
        this.cartRef = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(userId)
                .child("cart")
                .child("items");
    }

    public void getCartItems(OnCartLoadedListener listener) {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CartItem> cartItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem item = snapshot.getValue(CartItem.class);
                    cartItems.add(item);
                }
                listener.onCartLoaded(cartItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public void removeItem(String foodId, OnCartActionListener listener) {
        cartRef.child(foodId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Đã xóa món hàng"))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void clearCart(OnCartActionListener listener) {
        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Đã xóa toàn bộ giỏ hàng"))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnCartLoadedListener {
        void onCartLoaded(List<CartItem> cartItems);

        void onError(String errorMessage);
    }

    public interface OnCartActionListener {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}
