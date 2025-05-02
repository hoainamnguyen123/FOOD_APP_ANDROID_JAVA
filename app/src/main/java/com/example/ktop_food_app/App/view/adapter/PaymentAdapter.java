package com.example.ktop_food_app.App.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.PaymentItem;
import com.example.ktop_food_app.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<PaymentItem> paymentItems;
    private final DecimalFormat decimalFormat;

    // Constructor to initialize the list of payment items
    public PaymentAdapter(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems != null ? paymentItems : new ArrayList<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    // Method to update the list of payment items and notify the adapter of changes
    public void updateItems(List<PaymentItem> newItems) {
        this.paymentItems.clear();
        this.paymentItems.addAll(newItems != null ? newItems : new ArrayList<>());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        // Get the PaymentItem at the current position
        PaymentItem paymentItem = paymentItems.get(position);


        // Bind data to the views
        holder.foodTitleTextView.setText(paymentItem.getFoodTitle());
        holder.priceTextView.setText(decimalFormat.format(paymentItem.getPrice()) + " đ");
        holder.quantityTextView.setText(String.format("Quantity %d", paymentItem.getQuantity()));
        holder.totalItemPriceTextView.setText(decimalFormat.format(paymentItem.getTotalItemPrice()) + " đ");

        // Load the image using Glide if the image path is available
        if (paymentItem.getImagePath() != null && !paymentItem.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(paymentItem.getImagePath())
                    .placeholder(R.drawable.img_user) // Placeholder image while loading
                    .error(R.drawable.img_user) // Fallback image if loading fails
                    .into(holder.foodImageView);
        } else {
            holder.foodImageView.setImageResource(R.drawable.img_user); // Default image if no path is provided
        }
    }

    @Override
    public int getItemCount() {
        // Return the size of the payment items list, ensuring it is never null
        return paymentItems.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView;
        TextView foodTitleTextView, priceTextView, quantityTextView, totalItemPriceTextView;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views from the item layout
            foodImageView = itemView.findViewById(R.id.food_image_view);
            foodTitleTextView = itemView.findViewById(R.id.food_title_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            totalItemPriceTextView = itemView.findViewById(R.id.total_item_price_text_view);
        }
    }
}