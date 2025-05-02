package com.example.ktop_food_app.App.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.databinding.ItemOrderHistoryBinding;
import com.example.ktop_food_app.databinding.ItemTrackOrdersBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_TRACK_ORDER = 2;
    public static final int VIEW_TYPE_ORDER_HISTORY = 1;
    private final List<Order> orderList;
    private final Context context;
    private final OnOrderClickListener listener;
    private final DecimalFormat decimalFormat;
    private final int viewType; // To determine which layout to use

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener, int viewType) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.viewType = viewType;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    @Override
    public int getItemViewType(int position) {
        return viewType; // Use the viewType passed in constructor
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ORDER_HISTORY) {
            ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new OrderHistoryViewHolder(binding);
        } else if (viewType == VIEW_TYPE_TRACK_ORDER) {
            ItemTrackOrdersBinding binding = ItemTrackOrdersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TrackOrderViewHolder(binding);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (holder instanceof OrderHistoryViewHolder) {
            ((OrderHistoryViewHolder) holder).bind(order);
        } else if (holder instanceof TrackOrderViewHolder) {
            ((TrackOrderViewHolder) holder).bind(order);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // Listener interface for click events
    public interface OnOrderClickListener {
        void onOrderClick(Order order);

        void onReorderClick(Order order);

        void onReviewClick(Order order);
    }

    // ViewHolder for Order History
    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderHistoryViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtOrderId.setText(order.getOrderId());
            binding.txtOrderStatus.setText(order.getStatus());
            String allProduct = String.valueOf(order.getItems().size());
            binding.totalAllProduct.setText("(" + allProduct + " Products):");
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " đ");

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem item = order.getItems().get(0);
                Glide.with(context)
                        .load(item.getImagePath())
                        .into(binding.productImage);
                binding.productName.setText(item.getName());
                String quantityOfFirstProduct = String.valueOf(item.getQuantity());
                binding.productQuantity.setText("x" + quantityOfFirstProduct);
            }

            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
            binding.btnReorder.setOnClickListener(v -> listener.onReorderClick(order));
            binding.btnReviews.setOnClickListener(v -> listener.onReviewClick(order));
        }
    }

    // ViewHolder for Track Order
    public class TrackOrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemTrackOrdersBinding binding;

        public TrackOrderViewHolder(ItemTrackOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtOrderId.setText(order.getOrderId());
            binding.txtOrderStatus.setText(order.getStatus());
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " đ");

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem item = order.getItems().get(0);
                Glide.with(context)
                        .load(item.getImagePath())
                        .into(binding.productFirstImage);
                binding.productName.setText(item.getName());
            }
            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}