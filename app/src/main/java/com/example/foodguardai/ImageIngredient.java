package com.example.foodguardai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.foodguardai.models.NutritionalInfo;
import com.example.foodguardai.models.Product;
import com.example.foodguardai.logging.TestLog;
import com.example.foodguardai.logging.TestLogManager;
import com.example.foodguardai.utils.BannedProductChecker;
import com.example.foodguardai.utils.NutritionExtractor;
import com.example.foodguardai.utils.SafetyRatingCalculator;
import com.example.foodguardai.utils.TextRecognitionHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageIngredient extends AppCompatActivity {

    private static final String TAG = "ImageIngredient";
    private static final int CAMERA_PERMISSION_CODE = 101;

    private MaterialButton btnCamera, btnGallery, btnAnalyze, btnViewDetails;
    private ImageView ivPreview;
    private CircularProgressIndicator progressBar;
    private TextView tvStatus;

    private Uri capturedImageUri;
    private Bitmap currentBitmap;
    private TextRecognitionHelper textRecognizer;
    private long ocrStartTime = 0;

    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    // ── Static holder — passes OCR Product to ProductDetailsActivity ──────
    public static class OcrProductHolder {
        public static Product product;
        public static String  fullOcrText;
        public static void clear() { product = null; fullOcrText = null; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_ingredient);

        textRecognizer = new TextRecognitionHelper();
        initViews();
        registerLaunchers();
        setupButtons();
    }

    private void initViews() {
        btnCamera      = findViewById(R.id.btnCamera);
        btnGallery     = findViewById(R.id.btnGallery);
        btnAnalyze     = findViewById(R.id.btnAnalyze);
        btnViewDetails = findViewById(R.id.btnViewDetails);
        ivPreview      = findViewById(R.id.ivPreview);
        progressBar    = findViewById(R.id.progressBar);
        tvStatus       = findViewById(R.id.tvAnalysisHeader);

        btnAnalyze.setEnabled(false);
        btnViewDetails.setVisibility(View.GONE);
    }

    private void registerLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && capturedImageUri != null) {
                        try {
                            InputStream s = getContentResolver().openInputStream(capturedImageUri);
                            currentBitmap = BitmapFactory.decodeStream(s);
                            showPreview(currentBitmap);
                        } catch (Exception e) {
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            InputStream s = getContentResolver().openInputStream(uri);
                            currentBitmap = BitmapFactory.decodeStream(s);
                            showPreview(currentBitmap);
                        } catch (Exception e) {
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupButtons() {
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        });

        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnAnalyze.setOnClickListener(v -> {
            if (currentBitmap != null) runOcrAndAnalyze(currentBitmap);
        });

        // Opens ProductDetailsActivity with OCR result
        btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("FROM_OCR", true);
            startActivity(intent);
        });
    }

    // ─── Camera ──────────────────────────────────────────────────────────

    private void launchCamera() {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File f = File.createTempFile("IMG_" + ts, ".jpg", getCacheDir());
            capturedImageUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".provider", f);
            cameraLauncher.launch(capturedImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Could not launch camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPreview(Bitmap bmp) {
        ivPreview.setImageBitmap(bmp);
        ivPreview.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(true);
        btnViewDetails.setVisibility(View.GONE);
        tvStatus.setText("✅ Image loaded — tap Analyze Ingredients");
    }

    // ─── OCR + Analysis ──────────────────────────────────────────────────

    private void runOcrAndAnalyze(Bitmap bitmap) {
        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        btnViewDetails.setVisibility(View.GONE);
        tvStatus.setText("🔍 Reading label text...");
        ocrStartTime = System.currentTimeMillis();

        textRecognizer.recognizeText(
                InputImage.fromBitmap(bitmap, 0),
                new TextRecognitionHelper.TextRecognitionCallback() {
                    @Override
                    public void onSuccess(String text) {
                        runOnUiThread(() -> processText(text));
                    }

                    @Override
                    public void onFailure(String error) {
                        long latency = System.currentTimeMillis() - ocrStartTime;
                        TestLog ocrLog = TestLogManager.getInstance().createOcrLog(false, latency, error);
                        TestLogManager.getInstance().log(ocrLog);
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnAnalyze.setEnabled(true);
                            tvStatus.setText("❌ Could not read text. Try better lighting or a clearer photo.");
                        });
                    }
                });
    }

    private void processText(String fullText) {
        tvStatus.setText("⚙️ Analyzing ingredients...");
        long ocrLatency = System.currentTimeMillis() - ocrStartTime;

        // Extract ingredients section
        String ingredientsText = extractIngredientsSection(fullText);
        if (ingredientsText.isEmpty()) ingredientsText = fullText;

        // Ingredient safety analysis
        BannedProductChecker.IngredientAnalysisResult analysis =
                BannedProductChecker.analyzeIngredients(ingredientsText);

        // Build Product
        Product product = new Product();

        // Product name from first short line of OCR
        for (String line : fullText.split("\n")) {
            String t = line.trim();
            if (t.length() > 3 && t.length() < 60) {
                product.setProductName(t);
                break;
            }
        }
        if (product.getProductName() == null) product.setProductName("Scanned Product");
        product.setBrand("Label Scan");
        product.setIngredientsText(ingredientsText);

        // Try to extract nutrition values from label
        NutritionExtractor.ExtractedNutrition extracted =
                NutritionExtractor.extractNutritionFromText(fullText);
        if (extracted.success && extracted.nutritionalInfo != null) {
            product.setNutritionalInfo(extracted.nutritionalInfo);
        } else {
            product.setNutritionalInfo(new NutritionalInfo());
        }

        // Log OCR success with field extraction details
        NutritionExtractor.ExtractedNutrition fieldLog = extracted;
        com.example.foodguardai.models.NutritionalInfo ni = product.getNutritionalInfo();
        TestLog ocrFieldLog = TestLogManager.getInstance().createOcrFieldLog(
                ni.getProtein() > 0, ni.getProtein(),
                ni.getCarbohydrates() > 0, ni.getCarbohydrates(),
                ni.getFat() > 0, ni.getFat(),
                ni.getSodium() > 0, ni.getSodium(),
                ni.getSugar() > 0, ni.getSugar(),
                ni.getSaturatedFat() > 0, ni.getSaturatedFat(),
                ni.getFiber() > 0, ni.getFiber(),
                ocrLatency
        );
        TestLogManager.getInstance().log(ocrFieldLog);

        // Also log the top-level OCR success
        TestLog ocrLog = TestLogManager.getInstance().createOcrLog(true, ocrLatency, fullText.substring(0, Math.min(fullText.length(), 500)));
        TestLogManager.getInstance().log(ocrLog);

        // Set ban status
        product.setBanned(analysis.hasBannedIngredients);
        if (analysis.hasBannedIngredients) {
            product.setBanReason("Contains banned ingredients detected via label scan");
        }

        // Calculate safety rating (includes ingredient deductions)
        product.setSafetyRating(SafetyRatingCalculator.calculateSafetyRating(product));

        // Store in static holder for ProductDetailsActivity
        OcrProductHolder.product     = product;
        OcrProductHolder.fullOcrText = fullText;

        progressBar.setVisibility(View.GONE);
        btnAnalyze.setEnabled(true);

        // Quick verdict on this screen
        int rating = product.getSafetyRating();
        int flagged = analysis.flaggedIngredients.size();
        int safe    = analysis.safeIngredients.size();

        String verdictEmoji;
        switch (analysis.overallVerdict) {
            case "AVOID":            verdictEmoji = "❌ BANNED INGREDIENTS FOUND"; break;
            case "CAUTION":          verdictEmoji = "⚠️ HARMFUL ADDITIVES FOUND";  break;
            case "ALLERGEN_WARNING": verdictEmoji = "🚨 ALLERGEN WARNING";          break;
            case "SAFE":             verdictEmoji = "✅ INGREDIENTS LOOK SAFE";     break;
            default:                 verdictEmoji = "ℹ️ ANALYSIS COMPLETE";
        }

        tvStatus.setText(
                verdictEmoji + "\n" +
                        "Safety Score: " + rating + "/10\n" +
                        flagged + " flagged  •  " + safe + " safe ingredients\n\n" +
                        "Tap below for the full safety report →"
        );

        btnViewDetails.setVisibility(View.VISIBLE);
        btnViewDetails.setText("📋  View Full Safety Report");
    }

    // ─── Extract ingredients section ─────────────────────────────────────

    private String extractIngredientsSection(String fullText) {
        String lower = fullText.toLowerCase();
        String[] headers = {
                "ingredients:", "ingredients :", "ingredient list:",
                "contains:", "made with:", "composition:"
        };
        for (String header : headers) {
            int idx = lower.indexOf(header);
            if (idx >= 0) {
                String after = fullText.substring(idx + header.length()).trim();
                String[] endMarkers = {
                        "nutrition facts", "nutritional information", "per 100g",
                        "energy:", "calories:", "manufactured by",
                        "best before", "storage:", "allergy advice",
                        "warning:", "directions:"
                };
                int endIdx = after.length();
                String lowerAfter = after.toLowerCase();
                for (String m : endMarkers) {
                    int mIdx = lowerAfter.indexOf(m);
                    if (mIdx > 0 && mIdx < endIdx) endIdx = mIdx;
                }
                return after.substring(0, endIdx).trim();
            }
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == CAMERA_PERMISSION_CODE
                && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textRecognizer != null) textRecognizer.close();
    }
}