package com.example.ktop_food_app.App.model.repository;

import com.example.ktop_food_app.App.model.data.remote.FirebaseFoodData;

public class FoodRepository {
    private final FirebaseFoodData firebaseFoodData;

    public FoodRepository() {
        this.firebaseFoodData = new FirebaseFoodData();
    }

    public void getFoodList(FirebaseFoodData.FoodCallback callback) {
        firebaseFoodData.fetchFoods(callback);
    }
}
