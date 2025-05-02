package com.example.ktop_food_app.App.model.repository;

import com.example.ktop_food_app.App.model.data.remote.FirebaseCartData;

public class CartRepository {
    private final FirebaseCartData firebaseCart;

    public CartRepository(String userId) {
        this.firebaseCart = new FirebaseCartData(userId);
    }

    public void getCartItems(FirebaseCartData.OnCartLoadedListener listener) {
        firebaseCart.getCartItems(listener);
    }

    public void removeItem(String foodId, FirebaseCartData.OnCartActionListener listener) {
        firebaseCart.removeItem(foodId, listener);
    }

    public void clearCart(FirebaseCartData.OnCartActionListener listener) {
        firebaseCart.clearCart(listener);
    }
}
