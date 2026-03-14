package com.example.foodguardai.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class ApiManager {
    private static final String TAG = "ApiManager";

    // ⚠️ PASTE YOUR GEMINI API KEY HERE
    private static final String GEMINI_API_KEY = "AIzaSyC9h29X4SJVcFaFAD0MJONO-1Z2BgqB0qs";

    // Fixed URL - using v1 and gemini-pro (most compatible free model)
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void sendChatMessage(String message, String productContext, ApiCallback callback) {
        Log.d(TAG, "sendChatMessage called");

        if (GEMINI_API_KEY.equals("PASTE_YOUR_GEMINI_KEY_HERE") || GEMINI_API_KEY.isEmpty()) {
            callback.onSuccess("⚠️ API key not set. Get a free key at aistudio.google.com and paste it in ApiManager.java");
            return;
        }

        sendToGemini(message, productContext, callback, 0);
    }

    private static void sendToGemini(String message, String productContext,
                                     ApiCallback callback, int retryCount) {
        new Thread(() -> {
            try {
                if (retryCount > 0) {
                    Thread.sleep(3000L * retryCount);
                }

                String prompt = "You are a nutrition expert in a food safety app. " +
                        "Product: " + productContext + "\n" +
                        "User question: " + message + "\n" +
                        "Give a helpful answer in 2-3 sentences.";

                // Build Gemini request
                JsonObject textPart = new JsonObject();
                textPart.addProperty("text", prompt);

                JsonArray parts = new JsonArray();
                parts.add(textPart);

                JsonObject content = new JsonObject();
                content.add("parts", parts);

                JsonArray contents = new JsonArray();
                contents.add(content);

                JsonObject body = new JsonObject();
                body.add("contents", contents);

                String bodyStr = body.toString();
                Log.d(TAG, "Sending to Gemini: " + bodyStr);

                Request request = new Request.Builder()
                        .url(GEMINI_URL + GEMINI_API_KEY)
                        .post(RequestBody.create(bodyStr,
                                MediaType.parse("application/json")))
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = httpClient.newCall(request).execute();
                String responseStr = response.body() != null ? response.body().string() : "";
                int code = response.code();

                Log.d(TAG, "Response code: " + code);
                Log.d(TAG, "Response body: " + responseStr);

                if (code == 429) {
                    if (retryCount < 3) {
                        Log.w(TAG, "Rate limited, retrying...");
                        sendToGemini(message, productContext, callback, retryCount + 1);
                    } else {
                        callback.onSuccess("The AI is busy right now. Please wait a moment and try again.");
                    }
                    return;
                }

                if (response.isSuccessful()) {
                    JsonObject json = JsonParser.parseString(responseStr).getAsJsonObject();
                    String text = json
                            .getAsJsonArray("candidates")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts")
                            .get(0).getAsJsonObject()
                            .get("text").getAsString();

                    Log.d(TAG, "Gemini replied: " + text);
                    callback.onSuccess(text.trim());
                } else {
                    Log.e(TAG, "Error " + code + ": " + responseStr);
                    callback.onError("Error " + code + ": " + responseStr);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage(), e);
                callback.onError("Network error: " + e.getMessage());
            }
        }).start();
    }
}