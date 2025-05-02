package com.example.ktop_food_app.App.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final Context context;
    private OnTotalPriceChangedListener totalPriceChangedListener;
    private OnItemRemovedListener itemRemovedListener;
    private final DecimalFormat decimalFormat;

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
        if (context instanceof OnTotalPriceChangedListener) {
            this.totalPriceChangedListener = (OnTotalPriceChangedListener) context;
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.itemRemovedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(decimalFormat.format(item.getPrice()) + " đ");
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.totalPriceTextView.setText(decimalFormat.format(item.getTotalPrice()) + " đ");
        Glide.with(context).load(item.getImagePath()).into(holder.productImageView);

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartRef.child(item.getFoodId()).setValue(item); // Cập nhật lên Firebase
                notifyItemChanged(position);
                updateTotalPrice();
            } else {
                removeItem(position);
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            cartRef.child(item.getFoodId()).setValue(item); // Cập nhật lên Firebase
            notifyItemChanged(position);
            updateTotalPrice();
        });
    }

    private DatabaseReference cartRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("cart")
            .child("items");

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            String foodId = cartItems.get(position).getFoodId();
            cartItems.remove(position);
            cartRef.child(foodId).removeValue(); // Xóa khỏi Firebase
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            updateTotalPrice();

//            if (itemRemovedListener != null) {
//                itemRemovedListener.onItemRemoved(position);
//            }
        }
    }

    private void updateTotalPrice() {
        long totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        if (totalPriceChangedListener != null) {
            totalPriceChangedListener.onTotalPriceChanged(totalPrice);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface OnTotalPriceChangedListener {
        void onTotalPriceChanged(long totalPrice);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(int position);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView nameTextView, priceTextView, quantityTextView, totalPriceTextView;
        ImageButton decreaseButton, increaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            quantityTextView = itemView.findViewById(R.id.quantity_text);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            increaseButton = itemView.findViewById(R.id.increase_button);
        }
    }
}