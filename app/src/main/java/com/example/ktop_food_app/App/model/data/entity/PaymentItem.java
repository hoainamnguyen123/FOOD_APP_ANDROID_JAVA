package com.example.ktop_food_app.App.model.data.entity;

public class PaymentItem {
    private String foodTitle;
    private double price;
    private int quantity;
    private double totalItemPrice;
    private String imagePath;

    public PaymentItem(String foodTitle, double price, int quantity, double totalItemPrice, String imagePath) {
        this.foodTitle = foodTitle;
        this.price = price;
        this.quantity = quantity;
        this.totalItemPrice = totalItemPrice;
        this.imagePath = imagePath;
    }

    // Getters
    public String getFoodTitle() { return foodTitle; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotalItemPrice() { return totalItemPrice; }
    public String getImagePath() { return imagePath; }
}