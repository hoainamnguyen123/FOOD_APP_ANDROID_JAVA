package com.example.ktop_food_app.App.model.data.entity;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String address;
    private long createdAt;
    private double discount;
    private List<CartItem> items;
    private String orderId;
    private String paymentMethod;
    private String status;
    private double totalPrice;
    private String uid;

    // Constructor mặc định (yêu cầu bởi Firebase)
    public Order() {
    }

    public Order(String address, long createdAt, double discount, List<CartItem> items, String orderId,
                 String paymentMethod, String status, double totalPrice, String uid) {
        this.address = address;
        this.createdAt = createdAt;
        this.discount = discount;
        this.items = items;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.totalPrice = totalPrice;
        this.uid = uid;
    }

    // Getters và Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}