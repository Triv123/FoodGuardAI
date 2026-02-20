package com.example.foodguardai.api;


import com.example.foodguardai.models.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductApiService {
    // Open Food Facts API (Free, comprehensive food database)
    @GET("api/v0/product/{barcode}.json")
    Call<ProductResponse> getProductByBarcode(@retrofit2.http.Path("barcode") String barcode);
}