package com.example.ktop_food_app.App.model.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CartItem implements Parcelable, Serializable {
    private String foodId;
    private String name;
    private long price;
    private int quantity;
    private String imagePath;

    // Constructor mặc định (yêu cầu bởi Firebase)
    public CartItem() {}

    // Constructor với các tham số
    public CartItem(String name, long price, int quantity, String imagePath) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    // Constructor từ Parcel (yêu cầu bởi Parcelable)
    protected CartItem(Parcel in) {
        foodId = in.readString();
        name = in.readString();
        price = in.readLong();
        quantity = in.readInt();
        imagePath = in.readString();
    }

    // Creator cho Parcelable
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Các phương thức của Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(foodId);
        dest.writeString(name);
        dest.writeLong(price);
        dest.writeInt(quantity);
        dest.writeString(imagePath);
    }

    // Getters và Setters
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public long getTotalPrice() { return price * quantity; }
}