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

import com.bumptech.glide.Glide;
import com.example.foodguardai.api.RetrofitClient;
import com.example.foodguardai.api.ProductResponse;
import com.example.foodguardai.database.FirebaseManager;
import com.example.foodguardai.models.DietEntry;
import com.example.foodguardai.models.NutritionalInfo;
import com.example.foodguardai.models.Product;
import com.example.foodguardai.models.ScanHistory;
import com.example.foodguardai.utils.BannedProductChecker;
import com.example.foodguardai.utils.NutritionalAnalyzer;
import com.example.foodguardai.utils.SafetyRatingCalculator;
import com.example.foodguardai.logging.TestLog;
import com.example.foodguardai.logging.TestLogManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetails";

    private TextView tvProductName, tvBrand, tvCategory;
    private TextView tvSafetyRating, tvSafetyDesc, tvSafetyVerdict;
    private TextView tvCalories, tvProtein, tvCarbs, tvSugar;
    private TextView tvFat, tvSatFat, tvFiber, tvSodium;
    private TextView tvProteinLevel, tvCarbLevel, tvSugarLevel, tvSodiumLevel;
    private TextView tvIngredientsRaw;
    private LinearLayout llIngredientFlags, llSafeIngredients;
    private MaterialCardView cardIngredientWarning, cardIngredientSafe, cardBanned;
    private TextView tvIngredientWarningTitle, tvIngredientSafeTitle, tvBannedReason;
    private MaterialButton btnAskAI, btnAddToDiet;
    private CircularProgressIndicator progressBar;
    private ImageView ivProductImage;

    private Product currentProduct;
    private FirebaseManager firebaseManager;
    private long productFetchStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        firebaseManager = FirebaseManager.getInstance();
        initViews();

        boolean fromOcr = getIntent().getBooleanExtra("FROM_OCR", false);

        if (fromOcr) {
            Product ocrProduct = ImageIngredient.OcrProductHolder.product;
            if (ocrProduct != null) {
                displayProduct(ocrProduct);
                ImageIngredient.OcrProductHolder.clear();
            } else {
                Toast.makeText(this, "No scan data found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            String barcode = getIntent().getStringExtra("BARCODE");
            if (barcode != null) {
                fetchProductByBarcode(barcode);
            } else {
                Toast.makeText(this, "No product data", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initViews() {
        tvProductName         = findViewById(R.id.tvProductName);
        tvBrand               = findViewById(R.id.tvBrand);
        tvCategory            = findViewById(R.id.tvCategory);
        tvSafetyRating        = findViewById(R.id.tvSafetyRating);
        tvSafetyDesc          = findViewById(R.id.tvSafetyDesc);
        tvSafetyVerdict       = findViewById(R.id.tvSafetyVerdict);
        tvCalories            = findViewById(R.id.tvCalories);
        tvProtein             = findViewById(R.id.tvProtein);
        tvCarbs               = findViewById(R.id.tvCarbs);
        tvSugar               = findViewById(R.id.tvSugar);
        tvFat                 = findViewById(R.id.tvFat);
        tvSatFat              = findViewById(R.id.tvSatFat);
        tvFiber               = findViewById(R.id.tvFiber);
        tvSodium              = findViewById(R.id.tvSodium);
        tvProteinLevel        = findViewById(R.id.tvProteinLevel);
        tvCarbLevel           = findViewById(R.id.tvCarbLevel);
        tvSugarLevel          = findViewById(R.id.tvSugarLevel);
        tvSodiumLevel         = findViewById(R.id.tvSodiumLevel);
        tvIngredientsRaw      = findViewById(R.id.tvIngredientsRaw);
        llIngredientFlags     = findViewById(R.id.llIngredientFlags);
        llSafeIngredients     = findViewById(R.id.llSafeIngredients);
        cardIngredientWarning = findViewById(R.id.cardIngredientWarning);
        cardIngredientSafe    = findViewById(R.id.cardIngredientSafe);
        tvIngredientWarningTitle = findViewById(R.id.tvIngredientWarningTitle);
        tvIngredientSafeTitle    = findViewById(R.id.tvIngredientSafeTitle);
        btnAskAI              = findViewById(R.id.btnAskAI);
        btnAddToDiet          = findViewById(R.id.btnAddToDiet);
        progressBar           = findViewById(R.id.progressBar);
        ivProductImage        = findViewById(R.id.ivProductImage);
        cardBanned            = findViewById(R.id.cardBanned);
        tvBannedReason        = findViewById(R.id.tvBannedReason);
    }

    private void fetchProductByBarcode(String barcode) {
        progressBar.setVisibility(View.VISIBLE);
        productFetchStartTime = System.currentTimeMillis();
        RetrofitClient.getProductApi().getProductByBarcode(barcode)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        long latency = System.currentTimeMillis() - productFetchStartTime;
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                            Product p = mapApiToProduct(barcode, response.body());

                            TestLog barcodeLog = TestLogManager.getInstance().createBarcodeLog(
                                    barcode, true, true, latency,
                                    p.getProductName() != null ? p.getProductName() : "found");
                            barcodeLog.setProductName(p.getProductName());
                            TestLogManager.getInstance().log(barcodeLog);

                            displayProduct(p);
                            saveScanHistory(p);
                        } else {

                            TestLog barcodeLog = TestLogManager.getInstance().createBarcodeLog(
                                    barcode, true, false, latency,
                                    "product_not_found_off");
                            TestLogManager.getInstance().log(barcodeLog);

                            showNotFound(barcode);
                        }
                    }
                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        long latency = System.currentTimeMillis() - productFetchStartTime;
                        Log.e(TAG, "API failure", t);

                        TestLog barcodeLog = TestLogManager.getInstance().createBarcodeLog(
                                barcode, true, false, latency,
                                "api_failure: " + t.getMessage());
                        TestLogManager.getInstance().log(barcodeLog);

                        showNotFound(barcode);
                    }
                });
    }

    private Product mapApiToProduct(String barcode, ProductResponse resp) {
        ProductResponse.ProductData d = resp.getProduct();
        Product p = new Product();
        p.setBarcode(barcode);
        p.setProductName(d.getProductName() != null ? d.getProductName() : "Unknown Product");
        p.setBrand(d.getBrands() != null ? d.getBrands() : "");
        p.setCategory(d.getCategories() != null ? d.getCategories() : "");
        p.setImageUrl(d.getImageUrl());
        p.setIngredientsText(d.getIngredients());

        NutritionalInfo n = new NutritionalInfo();
        if (d.getNutriments() != null) {
            ProductResponse.Nutriments nm = d.getNutriments();
            n.setCalories(nm.getCalories());
            n.setProtein(nm.getProteins());
            n.setCarbohydrates(nm.getCarbohydrates());
            n.setSugar(nm.getSugars());
            n.setFat(nm.getFat());
            n.setSaturatedFat(nm.getSaturatedFat());
            n.setFiber(nm.getFiber());
            n.setSodium(nm.getSodium());
        }
        NutritionalAnalyzer.analyzeNutrients(n);
        p.setNutritionalInfo(n);

        BannedProductChecker.BanStatus ban = BannedProductChecker.checkIfBanned(p);
        p.setBanned(ban.isBanned());
        p.setBanReason(ban.getReason());
        p.setSafetyRating(SafetyRatingCalculator.calculateSafetyRating(p));
        return p;
    }

    private void displayProduct(Product p) {
        currentProduct = p;
        tvProductName.setText(p.getProductName() != null ? p.getProductName() : "Unknown");
        tvBrand.setText(p.getBrand() != null ? p.getBrand() : "");
        tvCategory.setText(p.getCategory() != null ? p.getCategory() : "");

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(this).load(p.getImageUrl()).into(ivProductImage);
            ivProductImage.setVisibility(View.VISIBLE);
        }

        int rating = p.getSafetyRating();
        tvSafetyRating.setText(rating + "/10");
        tvSafetyDesc.setText(SafetyRatingCalculator.getRatingDescription(rating));
        colorizeRating(rating);

        if (p.isBanned()) {
            cardBanned.setVisibility(View.VISIBLE);
            tvBannedReason.setText("⛔  " + p.getBanReason());
        } else {
            cardBanned.setVisibility(View.GONE);
        }

        NutritionalInfo n = p.getNutritionalInfo();
        if (n != null) {
            tvCalories.setText(String.format("%.0f kcal", n.getCalories()));
            tvProtein.setText(String.format("%.1f g", n.getProtein()));
            tvCarbs.setText(String.format("%.1f g", n.getCarbohydrates()));
            tvSugar.setText(String.format("%.1f g", n.getSugar()));
            tvFat.setText(String.format("%.1f g", n.getFat()));
            tvSatFat.setText(String.format("%.1f g", n.getSaturatedFat()));
            tvFiber.setText(String.format("%.1f g", n.getFiber()));
            tvSodium.setText(String.format("%.0f mg", n.getSodium() * 1000));
            if (n.getProteinLevel() != null) tvProteinLevel.setText(n.getProteinLevel());
            if (n.getCarbLevel()    != null) tvCarbLevel.setText(n.getCarbLevel());
            if (n.getSugarLevel()   != null) tvSugarLevel.setText(n.getSugarLevel());
            if (n.getSodiumLevel()  != null) tvSodiumLevel.setText(n.getSodiumLevel());
        }

        showIngredientAnalysis(p.getIngredientsText());
        btnAskAI.setOnClickListener(v -> openAIChat());
        btnAddToDiet.setOnClickListener(v -> addToDiet());
    }

    private void showIngredientAnalysis(String ingredientsText) {
        if (ingredientsText == null || ingredientsText.trim().isEmpty()) {
            tvIngredientsRaw.setText("No ingredient data available.");
            cardIngredientWarning.setVisibility(View.GONE);
            cardIngredientSafe.setVisibility(View.GONE);
            tvSafetyVerdict.setVisibility(View.GONE);
            return;
        }
        tvIngredientsRaw.setText(ingredientsText);
        BannedProductChecker.IngredientAnalysisResult result =
                BannedProductChecker.analyzeIngredients(ingredientsText);
        showVerdict(result);

        llIngredientFlags.removeAllViews();
        if (!result.flaggedIngredients.isEmpty()) {
            cardIngredientWarning.setVisibility(View.VISIBLE);
            tvIngredientWarningTitle.setText("⚠️  " + result.flaggedIngredients.size() + " Flagged Ingredient(s)");
            for (BannedProductChecker.IngredientFlag flag : result.flaggedIngredients)
                llIngredientFlags.addView(buildFlagRow(flag));
        } else {
            cardIngredientWarning.setVisibility(View.GONE);
        }

        llSafeIngredients.removeAllViews();
        if (!result.safeIngredients.isEmpty()) {
            cardIngredientSafe.setVisibility(View.VISIBLE);
            tvIngredientSafeTitle.setText("✅  " + result.safeIngredients.size() + " Safe Ingredient(s)");
            int limit = Math.min(result.safeIngredients.size(), 12);
            for (int i = 0; i < limit; i++)
                llSafeIngredients.addView(buildSafeRow(result.safeIngredients.get(i)));
            if (result.safeIngredients.size() > 12) {
                TextView more = new TextView(this);
                more.setText("+ " + (result.safeIngredients.size() - 12) + " more safe ingredients");
                more.setTextColor(Color.GRAY);
                more.setPadding(8, 4, 8, 4);
                llSafeIngredients.addView(more);
            }
        } else {
            cardIngredientSafe.setVisibility(View.GONE);
        }
    }

    private void showVerdict(BannedProductChecker.IngredientAnalysisResult r) {
        tvSafetyVerdict.setVisibility(View.VISIBLE);
        tvSafetyVerdict.setPadding(24, 16, 24, 16);
        switch (r.overallVerdict) {
            case "AVOID":
                tvSafetyVerdict.setText("❌  CONTAINS BANNED INGREDIENTS");
                tvSafetyVerdict.setBackgroundColor(0xFFFFEBEE);
                tvSafetyVerdict.setTextColor(0xFFB71C1C); break;
            case "CAUTION":
                tvSafetyVerdict.setText("⚠️  HARMFUL ADDITIVES — USE WITH CAUTION");
                tvSafetyVerdict.setBackgroundColor(0xFFFFF8E1);
                tvSafetyVerdict.setTextColor(0xFFE65100); break;
            case "ALLERGEN_WARNING":
                tvSafetyVerdict.setText("🚨  ALLERGEN PRESENT — CHECK BEFORE CONSUMING");
                tvSafetyVerdict.setBackgroundColor(0xFFFFF3E0);
                tvSafetyVerdict.setTextColor(0xFFBF360C); break;
            case "SAFE":
                tvSafetyVerdict.setText("✅  NO BANNED OR HARMFUL INGREDIENTS DETECTED");
                tvSafetyVerdict.setBackgroundColor(0xFFE8F5E9);
                tvSafetyVerdict.setTextColor(0xFF1B5E20); break;
            default:
                tvSafetyVerdict.setText("ℹ️  INGREDIENT DATA UNAVAILABLE");
                tvSafetyVerdict.setBackgroundColor(0xFFF5F5F5);
                tvSafetyVerdict.setTextColor(Color.DKGRAY);
        }
    }

    private View buildFlagRow(BannedProductChecker.IngredientFlag flag) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(16, 12, 16, 12);
        int bg;
        switch (flag.severity) {
            case "BANNED_INDIA": bg = 0xFFFFCDD2; break;
            case "BANNED_USA": case "BANNED_EU": bg = 0xFFFFE0B2; break;
            case "HARMFUL": bg = 0xFFFFFDE7; break;
            default: bg = 0xFFF3E5F5;
        }
        row.setBackgroundColor(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        row.setLayoutParams(lp);

        TextView name = new TextView(this);
        name.setText(severityEmoji(flag.severity) + "  " + flag.ingredient);
        name.setTextSize(14f);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        name.setTextColor(0xFF212121);
        row.addView(name);

        TextView reason = new TextView(this);
        reason.setText(flag.reason);
        reason.setTextSize(12f);
        reason.setTextColor(0xFF616161);
        row.addView(reason);

        TextView bannedIn = new TextView(this);
        bannedIn.setText("Restricted in: " + flag.bannedIn);
        bannedIn.setTextSize(11f);
        bannedIn.setTextColor(0xFF9E9E9E);
        row.addView(bannedIn);
        return row;
    }

    private View buildSafeRow(String name) {
        TextView tv = new TextView(this);
        tv.setText("✓  " + name);
        tv.setTextSize(13f);
        tv.setTextColor(0xFF388E3C);
        tv.setPadding(8, 4, 8, 4);
        return tv;
    }

    private String severityEmoji(String s) {
        switch (s) {
            case "BANNED_INDIA": return "🚫";
            case "BANNED_USA":   return "🇺🇸⛔";
            case "BANNED_EU":    return "🇪🇺⛔";
            case "HARMFUL":      return "⚠️";
            default:             return "🚨";
        }
    }

    private void colorizeRating(int rating) {
        int c;
        if (rating >= 8) c = 0xFF2E7D32;
        else if (rating >= 6) c = 0xFFF9A825;
        else if (rating >= 4) c = 0xFFE65100;
        else c = 0xFFC62828;
        tvSafetyRating.setTextColor(c);
        tvSafetyDesc.setTextColor(c);
    }

    private void showNotFound(String barcode) {
        tvProductName.setText("Product Not Found");
        tvBrand.setText("Barcode: " + barcode);
        tvCategory.setText("Not in Open Food Facts database");
        tvSafetyRating.setText("N/A");
        tvSafetyDesc.setText("No data available");
        tvIngredientsRaw.setText("Product not found.\n\nTry: Main screen → Scan Ingredient Label");
        cardIngredientWarning.setVisibility(View.GONE);
        cardIngredientSafe.setVisibility(View.GONE);
        tvSafetyVerdict.setVisibility(View.GONE);
        cardBanned.setVisibility(View.GONE);
    }

    private void saveScanHistory(Product p) {
        if (!firebaseManager.isUserLoggedIn()) return;
        ScanHistory s = new ScanHistory(firebaseManager.getCurrentUser().getUid(),
                p.getBarcode(), p.getProductName(), p.getBrand(), p.getSafetyRating(), "barcode");
        firebaseManager.addScanHistory(s, null);
    }

    private void openAIChat() {
        if (currentProduct == null) return;
        Intent i = new Intent(this, AIChatActivity.class);
        i.putExtra("PRODUCT_NAME", currentProduct.getProductName());
        i.putExtra("SAFETY_RATING", currentProduct.getSafetyRating());
        if (currentProduct.getIngredientsText() != null)
            i.putExtra("INGREDIENTS", currentProduct.getIngredientsText());
        startActivity(i);
    }

    private void addToDiet() {
        if (!firebaseManager.isUserLoggedIn()) {
            Toast.makeText(this, "Please login to track diet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentProduct == null || currentProduct.getNutritionalInfo() == null) {
            Toast.makeText(this, "No nutritional data to add", Toast.LENGTH_SHORT).show();
            return;
        }
        btnAddToDiet.setEnabled(false);
        DietEntry entry = new DietEntry(firebaseManager.getCurrentUser().getUid(),
                currentProduct.getProductName(), currentProduct.getBarcode(),
                currentProduct.getNutritionalInfo());
        firebaseManager.addDietEntry(entry, new FirebaseManager.DietCallback() {
            @Override public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(ProductDetailsActivity.this, "Added to diet ✓", Toast.LENGTH_SHORT).show();
                    btnAddToDiet.setText("Added ✓");
                    btnAddToDiet.setEnabled(true);
                });
            }
            @Override public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    btnAddToDiet.setEnabled(true);
                });
            }
        });
    }
}