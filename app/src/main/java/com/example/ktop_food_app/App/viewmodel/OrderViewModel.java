package com.example.ktop_food_app.App.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.model.data.remote.FirebaseOrderData;
import com.example.ktop_food_app.App.model.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> orderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMassage = new MutableLiveData<>();
    private final OrderRepository orderRepository;

    public OrderViewModel() {
        orderRepository = new OrderRepository();
        loadOrderList();
    }

    public MutableLiveData<List<Order>> getOrderListLiveData() {
        return orderListLiveData;
    }

    public MutableLiveData<String> getErrorMassage() {
        return errorMassage;
    }

    private void loadOrderList() {
        orderRepository.getOrderList(new FirebaseOrderData.OrderCallback() {
            @Override
            public void onSuccess(List<Order> orderList) {
                orderListLiveData.setValue(orderList);
            }

            @Override
            public void onFailure(String error) {
                errorMassage.setValue(error);
            }
        });
    }

    public void reloadOrders() {
        loadOrderList(); // Gọi lại như ban đầu
    }
}