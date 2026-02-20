package com.example.foodguardai.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ClaudeApiService {
    @POST("v1/messages")
    Call<ClaudeResponse> sendMessage(
            @Header("x-api-key") String apiKey,
            @Header("anthropic-version") String version,
            @Body RequestBody body
    );
}