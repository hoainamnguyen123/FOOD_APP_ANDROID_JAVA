package com.example.ktop_food_app.App.view.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app.App.model.data.entity.Category;
import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.view.adapter.FoodAdapter;
import com.example.ktop_food_app.App.viewmodel.FoodViewModel;
import com.example.ktop_food_app.databinding.ActivityFoodListBinding;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {
    private ActivityFoodListBinding binding; // ViewBinding
    private FoodAdapter foodAdapter;
    private Category category;
    private FoodViewModel foodViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        getCategoryFromIntent();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void initView() {
        binding.txtCategoryTitle.setText(""); // Để tránh lỗi null khi chưa nhận dữ liệu
    }

    private void getCategoryFromIntent() {
        Object serializableExtra = getIntent().getSerializableExtra("category");
        if (serializableExtra instanceof Category) {
            category = (Category) serializableExtra;
            binding.txtCategoryTitle.setText(category.getName());
        }
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foodAdapter = new FoodAdapter(this, new ArrayList<>(), null);
        binding.recyclerView.setAdapter(foodAdapter);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
    }

    private void setupViewModel() {
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        foodViewModel.getFoodListLiveData().observe(this, this::updateFoodList);
        foodViewModel.getErrorMessage().observe(this, errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        );
    }

    private void updateFoodList(List<Food> foodList) {
        if (foodList != null) {
            List<Food> filteredFoods = new ArrayList<>();
            for (Food food : foodList) {
                if (food.getCategoryId() == category.getId()) {
                    filteredFoods.add(food);
                }
            }
            foodAdapter.updateData(filteredFoods);
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(android.view.View view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                view.setLayoutParams(layoutParams);
            }

            @Override
            public void onChildViewDetachedFromWindow(android.view.View view) {
                // Không xử lý gì
            }
        });
    }

    // Lớp tạo khoảng cách giữa các item
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull android.view.View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) outRect.top = spacing;
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) outRect.top = spacing;
            }
        }
    }
}
