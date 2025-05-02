package com.example.ktop_food_app.App.model.data.entity;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String imagePath;

    // Constructor mặc định (yêu cầu bởi Firebase)
    public Category() {
    }

    public Category(String name, int id, String imagePath) {
        this.name = name;
        this.id = id;
        this.imagePath = imagePath;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}