package com.example.canonmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.canonmart.db.CartDBHelper;
import com.example.canonmart.model.CartItem;
import com.example.canonmart.fragment.CartFragment;
import com.example.canonmart.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private OnQuantityChangeListener onQuantityChangeListener;

    private boolean isLoading = false;

    public CartAdapter(List<CartItem> cartItemList, OnQuantityChangeListener onQuantityChangeListener) {
        this.cartItemList = cartItemList;
        this.onQuantityChangeListener = onQuantityChangeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, onQuantityChangeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        //        private TextView priceTextView;
        private TextView quantityTextView;
        private TextView totalItemPriceTextView;
        private ImageButton minusButton;
        private ImageButton plusButton;
        private ImageButton deleteButton;
        private CartItem currentItem;
        private OnQuantityChangeListener onQuantityChangeListener;
        private ImageView productImageView;
        // private ProgressBar itemProgressBar;

        public CartViewHolder(@NonNull View itemView, OnQuantityChangeListener onQuantityChangeListener) {
            super(itemView);
            this.onQuantityChangeListener = onQuantityChangeListener;

            nameTextView = itemView.findViewById(R.id.nameTextView);
//            priceTextView = itemView.findViewById(R.id.priceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityViewText);
            totalItemPriceTextView = itemView.findViewById(R.id.totalItemPriceTextView);
            minusButton = itemView.findViewById(R.id.minusButton);
            plusButton = itemView.findViewById(R.id.plusButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            productImageView = itemView.findViewById(R.id.productImageView);
            //itemProgressBar = itemView.findViewById(R.id.itemProgressBar); // Inisialisasi ProgressBar


            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = currentItem.getQuantity();
                    if (currentQuantity > 1) {
                        currentQuantity--;
                        currentItem.setQuantity(currentQuantity);
                        quantityTextView.setText(String.valueOf(currentQuantity));
                        totalItemPriceTextView.setText(String.format("Price: $%.2f", currentItem.getTotalPrice()));
                        // Update quantity in database
                        CartDBHelper dbHelper = new CartDBHelper(itemView.getContext());
                        dbHelper.updateCartItemQuantity(currentItem.getName(), currentQuantity);
                        onQuantityChangeListener.onQuantityChanged();
                    }
                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = currentItem.getQuantity();
                    if (currentQuantity < CartFragment.MAX_QUANTITY) {
                        currentQuantity++;
                        currentItem.setQuantity(currentQuantity);
                        quantityTextView.setText(String.valueOf(currentQuantity));
                        totalItemPriceTextView.setText(String.format("Price: $%.2f", currentItem.getTotalPrice()));
                        // Update quantity in database
                        CartDBHelper dbHelper = new CartDBHelper(itemView.getContext());
                        dbHelper.updateCartItemQuantity(currentItem.getName(), currentQuantity);
                        onQuantityChangeListener.onQuantityChanged();
                    }
                }
            });


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog();
                }
            });
        }

        private void showConfirmationDialog() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Konfirmsi Penghapusan")
                    .setMessage("Apakah Kamu Yakin Ingin Menghapus Produk Ini?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        removeAllItemsWithSameName(currentItem.getName());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        private void removeAllItemsWithSameName(String name) {
            CartDBHelper dbHelper = new CartDBHelper(itemView.getContext());
            dbHelper.removeAllCartItemsWithName(name); // Hapus dari database

            List<CartItem> itemsToRemove = new ArrayList<>();
            for (CartItem item : cartItemList) {
                if (item.getName().equals(name)) {
                    itemsToRemove.add(item);
                }
            }
            cartItemList.removeAll(itemsToRemove);
            notifyDataSetChanged();
            onQuantityChangeListener.onQuantityChanged();
        }

        public void bind(CartItem item) {
            currentItem = item;
            nameTextView.setText(item.getName());
//            priceTextView.setText(String.format("Price: $%.2f", item.getPrice()));
            quantityTextView.setText(String.valueOf(item.getQuantity()));
            totalItemPriceTextView.setText(String.format("Price: $%.2f", item.getTotalPrice()));
            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .error(R.drawable.error_image)
                    .into(productImageView);
        }

    }
}

