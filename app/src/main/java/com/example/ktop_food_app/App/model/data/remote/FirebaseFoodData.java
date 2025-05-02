package com.example.ktop_food_app.App.model.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ktop_food_app.App.model.data.entity.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFoodData {
    private final DatabaseReference foodRef;

    public FirebaseFoodData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app");
        foodRef = database.getReference("foods");
    }

    public void fetchFoods(FoodCallback callback) {
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foodList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Food food = data.getValue(Food.class);
                    if (food != null) {
                        foodList.add(food);
                    }
                }
                Log.d("FirebaseData", "Tải thành công " + foodList.size() + " món ăn.");
                callback.onSuccess(foodList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Lỗi tải dữ liệu món ăn: " + error.getMessage());
                callback.onFailure(error.getMessage());
            }
        });
    }

    public interface FoodCallback {
        void onSuccess(List<Food> foodList);

        void onFailure(String errorMessage);
    }
}
