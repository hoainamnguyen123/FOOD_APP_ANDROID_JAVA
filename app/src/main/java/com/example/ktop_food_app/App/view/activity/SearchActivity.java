package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.view.adapter.FoodAdapter;
import com.example.ktop_food_app.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private List<Food> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupRecyclerView();
        loadData();
        setupListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        filteredList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, filteredList, null);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));

        // Thêm listener để điều chỉnh kích thước item (giống FoodListActivity)
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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

    private void loadData() {
        Intent intent = getIntent();
        filteredList = (ArrayList<Food>) intent.getSerializableExtra("filtered_list");
        String query = intent.getStringExtra("search_query");

        // Cập nhật tiêu đề
        TextView txtFoodTitle = findViewById(R.id.txt_food_title);
        if (query != null && !query.isEmpty()) {
            txtFoodTitle.setText(query);
        }

        // Hiển thị danh sách kết quả
        if (filteredList != null) {
            foodAdapter.setFoodList(filteredList);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // Lớp tạo khoảng cách giữa các item (tương tự FoodListActivity)
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