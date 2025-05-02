package com.example.ktop_food_app.App.model.data.entity;

import java.io.Serializable;

public class Food implements Serializable {
    private String foodId;
    private String title;
    private String description;
    private int price;
    private float star;
    private String timeValue;
    private String imagePath;
    private int categoryId;
    private boolean bestFood;

    public Food() {
    }

    public Food(boolean bestFood, int categoryId, String imagePath, String timeValue, int price, float star, String description, String title, String foodId) {
        this.bestFood = bestFood;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
        this.timeValue = timeValue;
        this.price = price;
        this.star = star;
        this.description = description;
        this.title = title;
        this.foodId = foodId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isBestFood() {
        return bestFood;
    }

    public void setBestFood(boolean bestFood) {
        this.bestFood = bestFood;
    }
}
