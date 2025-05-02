package com.example.ktop_food_app.App.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.remote.FirebaseCartData;
import com.example.ktop_food_app.App.model.repository.CartRepository;

import java.util.List;

public class CartViewModel extends ViewModel {
    private final CartRepository cartRepository;
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Long> totalPrice = new MutableLiveData<>(0L);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CartViewModel(String userId) {
        cartRepository = new CartRepository(userId);
        loadCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Long> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadCartItems() {
        cartRepository.getCartItems(new FirebaseCartData.OnCartLoadedListener() {
            @Override
            public void onCartLoaded(List<CartItem> items) {
                cartItems.setValue(items);
                calculateTotalPrice(items);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void removeItem(String foodId) {
        cartRepository.removeItem(foodId, new FirebaseCartData.OnCartActionListener() {
            @Override
            public void onSuccess(String message) {
                loadCartItems();
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void clearCart() {
        cartRepository.clearCart(new FirebaseCartData.OnCartActionListener() {
            @Override
            public void onSuccess(String message) {
                cartItems.setValue(null);
                totalPrice.setValue(0L);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    private void calculateTotalPrice(List<CartItem> items) {
        long total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPrice.setValue(total);
    }
}
