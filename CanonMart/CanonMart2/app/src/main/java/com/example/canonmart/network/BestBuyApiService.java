package com.example.canonmart.network;

import com.example.canonmart.model.ProductApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BestBuyApiService {
    @GET("products(manufacturer=canon&salePrice%3C1000)")
    Call<ProductApiResponse> getProducts(
            @Query("format") String format,
            @Query("pageSize") int pageSize,
            @Query("show") String show,
            @Query("apiKey") String apiKey
    );

}


