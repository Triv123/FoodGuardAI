package com.example.foodguardai.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class ApiManager {
    private static final String TAG = "ApiManager";

    // FREE ALTERNATIVE: Use Gemini instead of Claude
    private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/";

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void sendChatMessage(String message, String productContext, ApiCallback callback) {
        Log.d(TAG, "Sending message: " + message);

        // Use Google Gemini (FREE API)
        sendToGemini(message, productContext, callback);
    }

    private static void sendToGemini(String message, String productContext, ApiCallback callback) {
        try {
            String prompt = "You are a nutrition expert. Based on this product: " +
                    productContext + "\n\nUser question: " + message +
                    "\n\nProvide a helpful, concise answer about nutrition and health.";

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("prompt", prompt);
            requestJson.addProperty("temperature", 0.7);
            requestJson.addProperty("maxTokens", 500);

            // For now, simulate response (replace with actual Gemini API call)
            simulateAIResponse(message, productContext, callback);

        } catch (Exception e) {
            Log.e(TAG, "Error sending to Gemini", e);
            callback.onError("Error: " + e.getMessage());
        }
    }

    // Temporary simulation until you add real API key
    private static void simulateAIResponse(String message, String context, ApiCallback callback) {
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simulate network delay

                String response = generateSmartResponse(message, context);
                callback.onSuccess(response);

            } catch (InterruptedException e) {
                callback.onError("Interrupted: " + e.getMessage());
            }
        }).start();
    }

    private static String generateSmartResponse(String message, String context) {
        String msg = message.toLowerCase();

        if (msg.contains("healthy") || msg.contains("good")) {
            return "Based on the nutritional profile, this product has moderate health value. " +
                    "Consider the sugar and sodium content. For a healthier choice, look for " +
                    "products with <5g sugar per 100g and <120mg sodium per 100g.";
        } else if (msg.contains("protein")) {
            return "Protein is essential for muscle building and repair. Adults need about " +
                    "50-60g daily. This product can contribute to your daily protein intake. " +
                    "Combine with vegetables and whole grains for balanced nutrition.";
        } else if (msg.contains("sugar")) {
            return "High sugar intake can lead to weight gain and health issues. The WHO " +
                    "recommends limiting added sugars to 25g per day. Check if this product " +
                    "fits within your daily sugar budget.";
        } else if (msg.contains("diet") || msg.contains("weight")) {
            return "For weight management, focus on whole foods, control portions, and " +
                    "maintain a calorie deficit. This product should be consumed in moderation. " +
                    "Track your daily intake to stay on target.";
        } else {
            return "I can help you understand nutrition labels and make healthier choices. " +
                    "Ask me about protein, sugar, sodium, calories, or whether this product " +
                    "is suitable for your diet goals!";
        }
    }
}