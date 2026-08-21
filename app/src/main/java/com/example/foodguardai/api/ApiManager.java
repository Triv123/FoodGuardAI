package com.example.foodguardai.api;

import android.util.Log;

import com.example.foodguardai.BuildConfig;
import com.example.foodguardai.logging.TestLog;
import com.example.foodguardai.logging.TestLogManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiManager {
    private static final String TAG = "ApiManager";

    // ── API key injected at build time from local.properties (gitignored) ──
    private static final String OPENROUTER_API_KEY = BuildConfig.OPENROUTER_API_KEY;
    // ───────────────────────────────────────────────────────────────────────

    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";

    // Updated March 2026 — working free models on OpenRouter
    // "openrouter/free" is the magic model: auto-picks whatever free model
    // is available right now, so it NEVER fails due to model name changes
    private static final String[] MODELS = {
            "openrouter/free",                              // Auto-router — always works
            "meta-llama/llama-4-scout:free",               // Meta Llama 4
            "meta-llama/llama-4-maverick:free",            // Meta Llama 4 Maverick
            "mistralai/mistral-small-3.1-24b-instruct:free", // Mistral Small 3.1
            "deepseek/deepseek-chat-v3-0324:free",         // DeepSeek V3
            "nvidia/llama-3.1-nemotron-nano-8b-v1:free",   // NVIDIA Nemotron
    };

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    // ── Entry point ───────────────────────────────────────────────────────

    public static void sendChatMessage(String message, String productContext,
                                       ApiCallback callback) {
        if (OPENROUTER_API_KEY.startsWith("PASTE_") || OPENROUTER_API_KEY.trim().isEmpty()) {
            callback.onError(
                    "API key not set.\n\n" +
                            "Go to openrouter.ai/keys → Create Key →\n" +
                            "add it to local.properties as OPENROUTER_API_KEY=..."
            );
            return;
        }
        new Thread(() -> tryModels(message, productContext, callback, 0)).start();
    }

    // ── Try each model in order ───────────────────────────────────────────

    private static void tryModels(String message, String productContext,
                                  ApiCallback callback, int index) {
        if (index >= MODELS.length) {
            callback.onError(
                    "Could not get a response right now.\n" +
                            "Please try again in a few seconds."
            );
            return;
        }

        String model = MODELS[index];
        Log.d(TAG, "Trying model [" + index + "]: " + model);
        long requestStart = System.currentTimeMillis();

        try {
            // Build messages
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "You are a friendly food safety and nutrition expert for Indian consumers. " +
                            "Keep answers to 2-4 sentences. Be clear, practical, and helpful.");

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", "Product: " + productContext + "\nQuestion: " + message);

            JSONArray messages = new JSONArray();
            messages.put(systemMsg);
            messages.put(userMsg);

            // Build request body
            JSONObject body = new JSONObject();
            body.put("model", model);
            body.put("messages", messages);
            body.put("max_tokens", 300);
            body.put("temperature", 0.7);
            body.put("stream", false);  // ← this was missing

            Request request = new Request.Builder()
                    .url(URL)
                    .post(RequestBody.create(
                            body.toString(),
                            MediaType.parse("application/json; charset=utf-8")))
                    .addHeader("Authorization", "Bearer " + OPENROUTER_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://foodguardai.app")
                    .addHeader("X-Title", "FoodGuardAI")
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                int code = response.code();
                String bodyStr = response.body() != null ? response.body().string() : "(empty)";

                Log.d(TAG, "Model=" + model + " HTTP=" + code);
                Log.d(TAG, "Body=" + bodyStr);

                if (code == 200) {
                    // Parse success
                    JSONObject json = new JSONObject(bodyStr);
                    String reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .trim();
                    long latency = System.currentTimeMillis() - requestStart;
                    Log.d(TAG, "✅ SUCCESS with: " + model);

                    TestLog aiLog = TestLogManager.getInstance().createAiLog(
                            "success", latency, "product_chat",
                            productContext, null, null, reply, model);
                    TestLogManager.getInstance().log(aiLog);

                    callback.onSuccess(reply);

                } else if (code == 401) {
                    // Wrong key — stop immediately, no point trying other models
                    long latency = System.currentTimeMillis() - requestStart;
                    Log.e(TAG, "❌ 401 Unauthorized — key is wrong");

                    TestLog aiLog = TestLogManager.getInstance().createAiLog(
                            "error_401", latency, "product_chat",
                            productContext, null, null, bodyStr, model);
                    TestLogManager.getInstance().log(aiLog);

                    callback.onError(
                            "API key is invalid (401).\n\n" +
                                    "Fix:\n" +
                                    "1. Go to openrouter.ai/keys\n" +
                                    "2. Delete old key → Create new key\n" +
                                    "3. Copy the FULL key\n" +
                                    "4. Update local.properties (OPENROUTER_API_KEY=...)"
                    );

                } else if (code == 429) {
                    // Rate limited — try next model
                    long latency = System.currentTimeMillis() - requestStart;
                    Log.w(TAG, "⚠️ 429 rate limit on " + model + " — trying next");

                    TestLog aiLog = TestLogManager.getInstance().createAiLog(
                            "error_429", latency, "product_chat",
                            productContext, null, null, bodyStr, model);
                    TestLogManager.getInstance().log(aiLog);

                    tryModels(message, productContext, callback, index + 1);

                } else if (code == 404) {
                    // Model not available — try next
                    long latency = System.currentTimeMillis() - requestStart;
                    Log.w(TAG, "⚠️ 404 model not found: " + model + " — trying next");

                    TestLog aiLog = TestLogManager.getInstance().createAiLog(
                            "error_404", latency, "product_chat",
                            productContext, null, null, bodyStr, model);
                    TestLogManager.getInstance().log(aiLog);

                    tryModels(message, productContext, callback, index + 1);

                } else {
                    long latency = System.currentTimeMillis() - requestStart;
                    Log.w(TAG, "⚠️ HTTP " + code + " on " + model + " — trying next");

                    TestLog aiLog = TestLogManager.getInstance().createAiLog(
                            "error_" + code, latency, "product_chat",
                            productContext, null, null, bodyStr, model);
                    TestLogManager.getInstance().log(aiLog);

                    tryModels(message, productContext, callback, index + 1);
                }
            }

        } catch (Exception e) {
            long latency = System.currentTimeMillis() - requestStart;
            Log.e(TAG, "Exception on " + model + ": " + e.getMessage(), e);

            TestLog aiLog = TestLogManager.getInstance().createAiLog(
                    "exception", latency, "product_chat",
                    productContext, null, null, e.getMessage(), model);
            TestLogManager.getInstance().log(aiLog);

            tryModels(message, productContext, callback, index + 1);
        }
    }
}