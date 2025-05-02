package com.example.ktop_food_app.App.model.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ktop_food_app.App.model.data.entity.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCategoryData {
    private final DatabaseReference databaseReference;

    public FirebaseCategoryData() {
        // Khởi tạo FirebaseDatabase với URL cụ thể
        FirebaseDatabase database = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app");
        // Tham chiếu đến node "categories"
        databaseReference = database.getReference("categories");
    }

    public void fetchCategories(final CategoryCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Category> categoryList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Category category = snapshot.getValue(Category.class);
                        if (category != null) {
                            categoryList.add(category);
                            Log.d("FirebaseData", "Category loaded: " + category.getName() + ", Image URL: " + category.getImagePath());
                        }
                    }
                    Log.d("FirebaseData", "Tổng số danh mục tải về: " + categoryList.size());
                } else {
                    Log.w("FirebaseData", "Không tìm thấy dữ liệu tại node 'categories'");
                }
                callback.onCategoriesLoaded(categoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Lỗi khi tải dữ liệu: " + databaseError.getMessage());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public interface CategoryCallback {
        void onCategoriesLoaded(List<Category> categoryList);

        void onError(String error);
    }
}
