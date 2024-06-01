package com.example.canonmart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.canonmart.activity.ProductDetailsActivity;
import com.example.canonmart.adapter.ProductAdapter;
import com.example.canonmart.model.Product;
import com.example.canonmart.model.ProductApiResponse;
import com.example.canonmart.network.BestBuyApiService;
import com.example.canonmart.network.RetrofitClient;
import com.example.canonmart.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensiveFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private LottieAnimationView progressBar;
    private SearchView searchView;
    private TextView noProductsTextView;
    private List<Product> productList;
    private Handler handler;
    private Runnable searchRunnable;

    public ExpensiveFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_expensive_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);
        noProductsTextView = view.findViewById(R.id.noProductsTextView);

        // Tampilkan progress bar saat memuat produk
        progressBar.playAnimation();
        progressBar.setVisibility(View.VISIBLE);

        handler = new Handler();

        // Delay selama 1 detik sebelum memuat produk
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadProducts();
            }
        }, 5000);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    progressBar.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> filterProducts(newText);
                    handler.postDelayed(searchRunnable, 3000);
                } else {
                    if (productList != null) {
                        productAdapter.updateData(productList);
                    }
                    progressBar.setVisibility(View.GONE);
                    noProductsTextView.setVisibility(View.GONE);
                }
                return true;
            }
        });

        return view;
    }

    private void loadProducts() {
        BestBuyApiService apiService = RetrofitClient.getClient().create(BestBuyApiService.class);
        Call<ProductApiResponse> productCall = apiService.getProducts(
                "json", 20, "sku,name,salePrice,image,color,longDescription", "GnOHjdEh0feKXImgF1k9sev3"
        );

        productCall.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body().getProducts();
                    Collections.sort(productList, (p1, p2) -> Double.compare(p2.getSalePrice(), p1.getSalePrice()));
                    productAdapter = new ProductAdapter(productList);
                    recyclerView.setAdapter(productAdapter);
                    noProductsTextView.setVisibility(View.GONE);

                    productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Product product) {
                            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
                // Sembunyikan progress bar setelah memuat produk
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
//                noProductsTextView.setVisibility(View.VISIBLE);
                // Sembunyikan progress bar jika gagal memuat produk
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterProducts(String query) {
        if (productList == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Product product : productList) {
            String salePriceString = String.valueOf(product.getSalePrice());
            if (product.getName().toLowerCase().contains(lowerCaseQuery) ||
                    product.getSku().toLowerCase().contains(lowerCaseQuery) ||
                    salePriceString.contains(lowerCaseQuery)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            noProductsTextView.setVisibility(View.VISIBLE);
        } else {
            noProductsTextView.setVisibility(View.GONE);
        }

        productAdapter.updateData(filteredList);
        progressBar.setVisibility(View.GONE);
    }

}




