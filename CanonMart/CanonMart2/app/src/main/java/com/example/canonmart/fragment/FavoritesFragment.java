package com.example.canonmart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canonmart.activity.MainActivity;
import com.example.canonmart.activity.ProductDetailsActivity;
import com.example.canonmart.adapter.ProductAdapter;
import com.example.canonmart.db.ProductDBHelper;
import com.example.canonmart.model.Product;
import com.example.canonmart.R;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> favoriteProducts;
    private ProductDBHelper dbHelper;
    private TextView emptyTextView;
    private static final int REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favorites_fragment, container, false);

        dbHelper = new ProductDBHelper(getContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyTextView = view.findViewById(R.id.emptyTextView);

        // Selalu perbarui produk favorit saat fragmen dibuat
        favoriteProducts = getAllFavoriteProducts();
        updateFavoriteProducts();

        ImageButton favoritesButton = getActivity().findViewById(R.id.favorites_button);
        favoritesButton.setColorFilter(getResources().getColor(R.color.black));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).updateButtonIcons("FavoritesFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).resetButtonIcons();
        }
    }

    private List<Product> getAllFavoriteProducts() {
        return dbHelper.getAllFavoriteProducts();
    }

    public void refreshFavoriteProducts() {
        favoriteProducts = getAllFavoriteProducts();
        updateFavoriteProducts();
    }

    private void updateFavoriteProducts() {
        if (favoriteProducts.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (productAdapter == null) {
                productAdapter = new ProductAdapter(favoriteProducts);
                recyclerView.setAdapter(productAdapter);

                productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Product product) {
                        startProductDetailsActivity(product);
                    }
                });
            } else {
                productAdapter.updateData(favoriteProducts);
            }
        }
    }

    private void startProductDetailsActivity(Product product) {
        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            String removedProductSku = data.getStringExtra("removed_product_sku");
            if (removedProductSku != null) {
                refreshFavoriteProducts();
            }
        }
    }
}
