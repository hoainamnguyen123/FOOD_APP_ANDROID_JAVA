package com.example.ktop_food_app.App.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.R;
import com.example.ktop_food_app.databinding.ItemTrackOrderDetailBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class TrackOrderDetailAdapter extends RecyclerView.Adapter<TrackOrderDetailAdapter.TrackOrderDetailViewHolder> {

    private final Context context;
    private final List<CartItem> itemList;
    private final DecimalFormat decimalFormat;

    public TrackOrderDetailAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    @NonNull
    @Override
    public TrackOrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout
        ItemTrackOrderDetailBinding binding = ItemTrackOrderDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TrackOrderDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderDetailViewHolder holder, int position) {
        CartItem item = itemList.get(position);

        // Set item name
        holder.binding.foodTitleTextView.setText(item.getName() != null ? item.getName() : "Unknown Product");

        // Set quantity
        holder.binding.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Set price and total price
        holder.binding.priceTextView.setText(decimalFormat.format(item.getPrice()) + " đ");
        holder.binding.totalItemPriceTextView.setText(decimalFormat.format(item.getTotalPrice()) + " đ");

        // Load image using Glide
        Glide.with(context)
                .load(item.getImagePath())
                .placeholder(R.drawable.bingsuberry)
                .error(R.drawable.bingsuberry)
                .into(holder.binding.foodImageView);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    static class TrackOrderDetailViewHolder extends RecyclerView.ViewHolder {
        private final ItemTrackOrderDetailBinding binding;

        public TrackOrderDetailViewHolder(@NonNull ItemTrackOrderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
