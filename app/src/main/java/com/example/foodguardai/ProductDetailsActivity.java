package com.example.foodguardai;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.foodguardai.api.ProductApiService;
import com.example.foodguardai.api.ProductResponse;
import com.example.foodguardai.api.RetrofitClient;
import com.example.foodguardai.database.FirebaseManager;
import com.example.foodguardai.models.DietEntry;
import com.example.foodguardai.models.NutritionalInfo;
import com.example.foodguardai.models.Product;
import com.example.foodguardai.models.ScanHistory;
import com.example.foodguardai.utils.BannedProductChecker;
import com.example.foodguardai.utils.NutritionalAnalyzer;
import com.example.foodguardai.utils.SafetyRatingCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailsActivity";

    private CircularProgressIndicator progressBar;
    private NestedScrollView layoutContent;
    private LinearLayout layoutError;
    private MaterialButton btnAddToDiet;
    private FirebaseManager firebaseManager;
    private ImageView imgProduct;
    private TextView tvProductName;
    private TextView tvBrand;
    private TextView tvCategory;
    private Chip chipBannedStatus;
    private CardView cardSafetyRating;
    private TextView tvSafetyRating;
    private TextView tvSafetyDescription;
    private View safetyRatingBar;

    private TextView tvCalories;
    private TextView tvProtein;
    private TextView tvProteinLevel;
    private TextView tvCarbs;
    private TextView tvCarbLevel;
    private TextView tvSugar;
    private TextView tvSugarLevel;
    private TextView tvFat;
    private TextView tvSaturatedFat;
    private TextView tvFiber;
    private TextView tvSodium;
    private TextView tvSodiumLevel;

    private MaterialButton btnAskAI;
    private MaterialButton btnRetry;

    private String barcode;
    private Product currentProduct;
    private boolean historyAlreadySaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_product_details);

            barcode = getIntent().getStringExtra("BARCODE");
            Log.d(TAG, "Barcode received: " + barcode);
            firebaseManager = FirebaseManager.getInstance();

            if (!initViews()) {
                Toast.makeText(this, "Error loading screen", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            setupClickListeners();

            if (barcode != null && !barcode.isEmpty()) {
                fetchProductData(barcode);
            } else {
                showError("Invalid barcode received");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Critical error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean initViews() {
        try {
            progressBar = findViewById(R.id.progressBar);
            layoutContent = findViewById(R.id.layoutContent);
            layoutError = findViewById(R.id.layoutError);
            btnAddToDiet = findViewById(R.id.btnAddToDiet);

            if (progressBar == null || layoutContent == null || layoutError == null) {
                Log.e(TAG, "Core views are null!");
                return false;
            }

            imgProduct = findViewById(R.id.imgProduct);
            tvProductName = findViewById(R.id.tvProductName);
            tvBrand = findViewById(R.id.tvBrand);
            tvCategory = findViewById(R.id.tvCategory);
            chipBannedStatus = findViewById(R.id.chipBannedStatus);

            cardSafetyRating = findViewById(R.id.cardSafetyRating);
            tvSafetyRating = findViewById(R.id.tvSafetyRating);
            tvSafetyDescription = findViewById(R.id.tvSafetyDescription);
            safetyRatingBar = findViewById(R.id.safetyRatingBar);

            tvCalories = findViewById(R.id.tvCalories);
            tvProtein = findViewById(R.id.tvProtein);
            tvProteinLevel = findViewById(R.id.tvProteinLevel);
            tvCarbs = findViewById(R.id.tvCarbs);
            tvCarbLevel = findViewById(R.id.tvCarbLevel);
            tvSugar = findViewById(R.id.tvSugar);
            tvSugarLevel = findViewById(R.id.tvSugarLevel);
            tvFat = findViewById(R.id.tvFat);
            tvSaturatedFat = findViewById(R.id.tvSaturatedFat);
            tvFiber = findViewById(R.id.tvFiber);
            tvSodium = findViewById(R.id.tvSodium);
            tvSodiumLevel = findViewById(R.id.tvSodiumLevel);

            btnAskAI = findViewById(R.id.btnAskAI);
            btnRetry = findViewById(R.id.btnRetry);

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            return false;
        }
    }

    private void setupClickListeners() {
        if (btnAskAI != null) {
            btnAskAI.setOnClickListener(v -> {
                if (currentProduct != null) {
                    openAIChat();
                } else {
                    Toast.makeText(this, "Product data not loaded yet", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                if (barcode != null) fetchProductData(barcode);
            });
        }

        if (btnAddToDiet != null) {
            btnAddToDiet.setOnClickListener(v -> {
                if (currentProduct != null) addToDiet();
            });
        }
    }

    private void addToDiet() {
        if (!firebaseManager.isUserLoggedIn()) {
            Toast.makeText(this, "Please login to track your diet", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (currentProduct.getNutritionalInfo() == null) {
            Toast.makeText(this, "No nutritional data available for this product",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (btnAddToDiet != null) {
            btnAddToDiet.setEnabled(false);
            btnAddToDiet.setText("Adding...");
        }

        String userId = firebaseManager.getCurrentUser().getUid();

        DietEntry entry = new DietEntry(
                userId,
                currentProduct.getProductName(),
                currentProduct.getBarcode(),
                currentProduct.getNutritionalInfo()
        );

        firebaseManager.addDietEntry(entry, new FirebaseManager.DietCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    if (btnAddToDiet != null) {
                        btnAddToDiet.setEnabled(true);
                        btnAddToDiet.setText("✓ Added to Diet");
                        btnAddToDiet.setBackgroundColor(
                                ContextCompat.getColor(ProductDetailsActivity.this,
                                        android.R.color.holo_green_dark));
                    }
                    Toast.makeText(ProductDetailsActivity.this,
                            "Added to your diet tracker!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (btnAddToDiet != null) {
                        btnAddToDiet.setEnabled(true);
                        btnAddToDiet.setText("Add to My Diet");
                    }
                    Toast.makeText(ProductDetailsActivity.this,
                            "Failed to add: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveToScanHistory() {
        if (!firebaseManager.isUserLoggedIn() || historyAlreadySaved) return;
        if (currentProduct == null) return;

        historyAlreadySaved = true;

        String userId = firebaseManager.getCurrentUser().getUid();
        ScanHistory scan = new ScanHistory(
                userId,
                currentProduct.getBarcode(),
                currentProduct.getProductName(),
                currentProduct.getBrand(),
                currentProduct.getSafetyRating(),
                "barcode"
        );

        firebaseManager.addScanHistory(scan, new FirebaseManager.DietCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Scan history saved automatically");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to save scan history: " + error);
                historyAlreadySaved = false;
            }
        });
    }

    private void fetchProductData(String barcode) {
        Log.d(TAG, "Fetching product: " + barcode);
        showLoading();

        try {
            ProductApiService apiService = RetrofitClient.getProductApi();
            Call<ProductResponse> call = apiService.getProductByBarcode(barcode);

            call.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call,
                                       Response<ProductResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ProductResponse productResponse = response.body();

                            if (productResponse.getStatus() == 1
                                    && productResponse.getProduct() != null) {
                                processProductData(productResponse.getProduct());
                            } else {
                                showError("Product not found in database.\n\n" +
                                        "Try these test barcodes:\n" +
                                        "• 3017620422003 (Nutella)\n" +
                                        "• 5449000000996 (Coca-Cola)\n" +
                                        "• 8076809513456 (Pasta)");
                            }
                        } else {
                            showError("API Error: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing response", e);
                        showError("Error: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    showError("Network error. Please check your connection.\n\n" + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error making API call", e);
            showError("Error: " + e.getMessage());
        }
    }

    private void processProductData(ProductResponse.ProductData productData) {
        try {
            currentProduct = new Product();
            currentProduct.setBarcode(barcode);

            String productName = productData.getProductName();
            currentProduct.setProductName(productName != null ? productName : "Unknown Product");
            currentProduct.setBrand(productData.getBrands());
            currentProduct.setCategory(productData.getCategories());
            currentProduct.setImageUrl(productData.getImageUrl());

            if (productData.getNutriments() != null) {
                NutritionalInfo nutritionalInfo = new NutritionalInfo();
                nutritionalInfo.setServingSize(100);
                nutritionalInfo.setServingUnit("g");
                nutritionalInfo.setCalories(productData.getNutriments().getCalories());
                nutritionalInfo.setProtein(productData.getNutriments().getProteins());
                nutritionalInfo.setCarbohydrates(productData.getNutriments().getCarbohydrates());
                nutritionalInfo.setSugar(productData.getNutriments().getSugars());
                nutritionalInfo.setFat(productData.getNutriments().getFat());
                nutritionalInfo.setSaturatedFat(productData.getNutriments().getSaturatedFat());
                nutritionalInfo.setFiber(productData.getNutriments().getFiber());
                nutritionalInfo.setSodium(productData.getNutriments().getSodium());

                NutritionalAnalyzer.analyzeNutrients(nutritionalInfo);
                currentProduct.setNutritionalInfo(nutritionalInfo);
            }

            BannedProductChecker.BanStatus banStatus =
                    BannedProductChecker.checkIfBanned(currentProduct);
            currentProduct.setBanned(banStatus.isBanned());
            currentProduct.setBanReason(banStatus.getReason());

            int safetyRating = SafetyRatingCalculator.calculateSafetyRating(currentProduct);
            currentProduct.setSafetyRating(safetyRating);

            runOnUiThread(() -> {
                displayProduct();
                saveToScanHistory();
            });

        } catch (Exception e) {
            Log.e(TAG, "Error processing product data", e);
            runOnUiThread(() -> showError("Processing Error: " + e.getMessage()));
        }
    }

    private void displayProduct() {
        try {
            hideLoading();
            layoutContent.setVisibility(View.VISIBLE);
            layoutError.setVisibility(View.GONE);
            if (btnAddToDiet != null) btnAddToDiet.setVisibility(View.VISIBLE);

            if (imgProduct != null && currentProduct.getImageUrl() != null) {
                Glide.with(this)
                        .load(currentProduct.getImageUrl())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(imgProduct);
            }

            if (tvProductName != null) tvProductName.setText(currentProduct.getProductName());
            if (tvBrand != null) tvBrand.setText(
                    currentProduct.getBrand() != null ? currentProduct.getBrand() : "Unknown Brand");
            if (tvCategory != null) tvCategory.setText(
                    currentProduct.getCategory() != null
                            ? currentProduct.getCategory() : "Unknown Category");

            if (chipBannedStatus != null) {
                if (currentProduct.isBanned()) {
                    chipBannedStatus.setVisibility(View.VISIBLE);
                    chipBannedStatus.setText("⚠️ BANNED - " + currentProduct.getBanReason());
                } else {
                    chipBannedStatus.setVisibility(View.GONE);
                }
            }

            displaySafetyRating(currentProduct.getSafetyRating());

            if (currentProduct.getNutritionalInfo() != null) {
                displayNutritionalInfo(currentProduct.getNutritionalInfo());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error displaying product", e);
            showError("Display Error: " + e.getMessage());
        }
    }

    private void displaySafetyRating(int rating) {
        try {
            if (tvSafetyRating != null) tvSafetyRating.setText(rating + "/10");
            if (tvSafetyDescription != null)
                tvSafetyDescription.setText(SafetyRatingCalculator.getRatingDescription(rating));

            int color;
            if (rating >= 8) color = Color.parseColor("#4CAF50");
            else if (rating >= 6) color = Color.parseColor("#8BC34A");
            else if (rating >= 4) color = Color.parseColor("#FFC107");
            else if (rating >= 2) color = Color.parseColor("#FF9800");
            else color = Color.parseColor("#F44336");

            if (safetyRatingBar != null) {
                int maxWidth = 300;
                int width = (int) (maxWidth * (rating / 10.0));
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) safetyRatingBar.getLayoutParams();
                params.width = (int) (width * (int) getResources().getDisplayMetrics().density);
                safetyRatingBar.setLayoutParams(params);
                safetyRatingBar.setBackgroundColor(color);
            }
            
            if (tvSafetyRating != null && tvSafetyRating.getBackground() != null) {
                tvSafetyRating.getBackground().setTint(color);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying safety rating", e);
        }
    }

    private void displayNutritionalInfo(NutritionalInfo info) {
        try {
            if (tvCalories != null)
                tvCalories.setText(String.format("%.0f kcal", info.getCalories()));
            if (tvProtein != null)
                tvProtein.setText(String.format("%.1f g", info.getProtein()));
            if (tvProteinLevel != null) {
                tvProteinLevel.setText(info.getProteinLevel());
                setLevelColor(tvProteinLevel, info.getProteinLevel());
            }
            if (tvCarbs != null)
                tvCarbs.setText(String.format("%.1f g", info.getCarbohydrates()));
            if (tvCarbLevel != null) {
                tvCarbLevel.setText(info.getCarbLevel());
                setLevelColor(tvCarbLevel, info.getCarbLevel());
            }
            if (tvSugar != null)
                tvSugar.setText(String.format("%.1f g", info.getSugar()));
            if (tvSugarLevel != null) {
                tvSugarLevel.setText(info.getSugarLevel());
                setLevelColor(tvSugarLevel, info.getSugarLevel());
            }
            if (tvFat != null)
                tvFat.setText(String.format("%.1f g", info.getFat()));
            if (tvSaturatedFat != null)
                tvSaturatedFat.setText(String.format("%.1f g", info.getSaturatedFat()));
            if (tvFiber != null)
                tvFiber.setText(String.format("%.1f g", info.getFiber()));

            double sodiumMg = info.getSodium() * 1000;
            if (tvSodium != null)
                tvSodium.setText(String.format("%.0f mg", sodiumMg));
            if (tvSodiumLevel != null) {
                tvSodiumLevel.setText(info.getSodiumLevel());
                setLevelColor(tvSodiumLevel, info.getSodiumLevel());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying nutrition info", e);
        }
    }

    private void setLevelColor(TextView textView, String level) {
        if (textView == null || level == null) return;
        if (level.contains("High") || level.contains("Excessive")) {
            textView.setTextColor(Color.parseColor("#F44336"));
        } else if (level.contains("Low")) {
            textView.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            textView.setTextColor(Color.parseColor("#FF9800"));
        }
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (layoutContent != null) layoutContent.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            hideLoading();
            if (layoutContent != null) layoutContent.setVisibility(View.GONE);
            if (layoutError != null) layoutError.setVisibility(View.VISIBLE);

            TextView tvError = findViewById(R.id.tvError);
            if (tvError != null) tvError.setText(message);
        });
    }

    private void openAIChat() {
        try {
            Intent intent = new Intent(this, AIChatActivity.class);
            intent.putExtra("PRODUCT_NAME", currentProduct.getProductName());
            intent.putExtra("PRODUCT_BARCODE", currentProduct.getBarcode());
            intent.putExtra("SAFETY_RATING", currentProduct.getSafetyRating());
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening AI chat", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}