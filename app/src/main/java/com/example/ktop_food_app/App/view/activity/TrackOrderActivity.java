package com.example.ktop_food_app.App.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.view.adapter.FoodAdapter;
import com.example.ktop_food_app.App.view.adapter.OrderAdapter;
import com.example.ktop_food_app.App.viewmodel.FoodViewModel;
import com.example.ktop_food_app.App.viewmodel.OrderViewModel;
import com.example.ktop_food_app.databinding.ActivityTrackOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class TrackOrderActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private final List<Order> orderList = new ArrayList<>();
    private final List<Food> foodList = new ArrayList<>();
    private ActivityTrackOrdersBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;
    private FoodAdapter foodAdapter;
    private FoodViewModel foodViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecyclerView();
        initViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderViewModel != null) {
            orderViewModel.reloadOrders();
        }
    }

    private void initRecyclerView() {
        // Use VIEW_TYPE_TRACK_ORDER for TrackOrderActivity
        adapter = new OrderAdapter(this, orderList, this, OrderAdapter.VIEW_TYPE_TRACK_ORDER);
        binding.trackOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.trackOrders.setAdapter(adapter);

        foodAdapter = new FoodAdapter(this, foodList, null);
        binding.viewAllFoods.setLayoutManager(new GridLayoutManager(this, 2));
        binding.viewAllFoods.setAdapter(foodAdapter);
        binding.viewAllFoods.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
    }

    private void initViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderListLiveData().observe(this, orders -> {
            orderList.clear();
            for (Order order : orders) {
                if ("pending".equals(order.getStatus()) || "shipping".equals(order.getStatus())) {
                    orderList.add(order);
                }
            }
            adapter.notifyDataSetChanged();
        });

        // Load data from FoodViewModel
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        foodViewModel.getFoodListLiveData().observe(this, foods -> {
            foodList.clear();
            foodList.addAll(foods);
            foodAdapter.notifyDataSetChanged();
        });

        orderViewModel.getErrorMassage().observe(this, error -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.viewAllFoods.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(android.view.View view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                view.setLayoutParams(layoutParams);
            }

            @Override
            public void onChildViewDetachedFromWindow(android.view.View view) {
                // No action needed
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(TrackOrderActivity.this, TrackOrderDetailActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }

    // Implement remaining listener methods (not used in TrackOrderActivity but required by interface)
    @Override
    public void onReorderClick(Order order) {
        // Not applicable for TrackOrderActivity, leave empty or add logic if needed
    }

    @Override
    public void onReviewClick(Order order) {
        // Not applicable for TrackOrderActivity, leave empty or add logic if needed
    }

    // GridSpacingItemDecoration class remains unchanged
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