package com.example.ktop_food_app.App.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.view.activity.FoodDetailActivity;
import com.example.ktop_food_app.databinding.ItemFoodBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final Context context;
    private final OnFoodClickListener onFoodClickListener;
    private List<Food> foodList;

    // Constructor nhận context, danh sách món ăn, và listener
    public FoodAdapter(Context context, List<Food> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        this.onFoodClickListener = new OnFoodClickListener() {
            @Override
            public void onFoodClick(Food food) {
                // Truyền đối tượng Food và mở FoodDetailActivity trực tiếp
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food", food);
                context.startActivity(intent);
            }
        };
    }

    // Phương thức cập nhật danh sách món ăn
    public void setFoodList(List<Food> newFoodList) {
        this.foodList = newFoodList != null ? newFoodList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding, context, onFoodClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateData(List<Food> newList) {
        foodList = newList;
        notifyDataSetChanged();
    }

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding;
        private final Context context;
        private final OnFoodClickListener onFoodClickListener;
        private final DecimalFormat decimalFormat;

        public FoodViewHolder(@NonNull ItemFoodBinding binding, Context context, OnFoodClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            this.onFoodClickListener = listener;

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            decimalFormat = new DecimalFormat("#,###", symbols);
        }

        public void bind(Food food) {
            binding.txtFoodName.setText(food.getTitle());

            binding.txtFoodPrice.setText(decimalFormat.format(food.getPrice())+ " đ");
            binding.txtFoodRating.setText(String.format("%.1f", food.getStar()));
            binding.txtFoodTime.setText(food.getTimeValue());
            Glide.with(context).load(food.getImagePath()).into(binding.imgFood);

            // Xử lý sự kiện click bằng cách gọi listener
            binding.getRoot().setOnClickListener(v -> {
                if (onFoodClickListener != null) {
                    onFoodClickListener.onFoodClick(food);
                }
            });
        }
    }

}