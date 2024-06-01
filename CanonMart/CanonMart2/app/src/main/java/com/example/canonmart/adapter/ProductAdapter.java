package com.example.canonmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canonmart.model.Product;
import com.example.canonmart.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setFilteredList(List<Product> filteredList) {
        this.products = filteredList;
        notifyDataSetChanged();
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("Price: $" + product.getSalePrice());
        holder.productSkuTextView.setText("SKU: " + product.getSku());

        // Tampilkan ProgressBar dan sembunyikan ImageView saat gambar sedang dimuat
        holder.productProgressBar.setVisibility(View.VISIBLE);
        holder.productImageView.setVisibility(View.GONE);

        Picasso.get().load(product.getImage()).into(holder.productImageView, new Callback() {
            @Override
            public void onSuccess() {
                // Sembunyikan ProgressBar dan tampilkan ImageView setelah gambar dimuat
                holder.productProgressBar.setVisibility(View.GONE);
                holder.productImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                // Sembunyikan ProgressBar jika ada kesalahan
                holder.productProgressBar.setVisibility(View.GONE);
                // Tampilkan placeholder atau lakukan tindakan lain jika diperlukan
                holder.productImageView.setImageResource(R.drawable.placeholder_image);
                holder.productImageView.setVisibility(View.VISIBLE);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, productPriceTextView, productSkuTextView;
        ImageView productImageView;
        ProgressBar productProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productSkuTextView = itemView.findViewById(R.id.productSkuTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productProgressBar = itemView.findViewById(R.id.productProgressBar);
        }
    }
}
