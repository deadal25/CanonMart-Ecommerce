package com.example.canonmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.canonmart.model.CartItem;
import com.example.canonmart.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<CartItem> orderItemList;


    public OrderAdapter(List<CartItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView quantityTextView;
        private TextView totalPriceTextView;
        private ImageView productImageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);

        }

        public void bind(CartItem item) {
            nameTextView.setText(item.getName());
            quantityTextView.setText("Quantity: " + item.getQuantity());
            totalPriceTextView.setText(String.format("Price: $%.2f", item.getTotalPrice()));

            Glide.with(productImageView.getContext())
                    .load(item.getImageUrl())
                    .into(productImageView);

        }
    }

}

