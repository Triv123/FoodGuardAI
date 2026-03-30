package com.example.foodguardai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.foodguardai.utils.TextRecognitionHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";
    private PreviewView previewView;
    private TextView tvScanInstruction;
    private MaterialButton btnManualEntry;
    private CircularProgressIndicator progressIndicator;

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private ProcessCameraProvider cameraProvider;
    private boolean isScanning = true;
    private boolean isProcessing = false;
    private long lastScanTime = 0;

    private MaterialButton btnToggleMode;
    private TextView tvModeIndicator;
    private boolean isOcrMode = false;
    private TextRecognitionHelper textRecognizer;
    private static final long SCAN_COOLDOWN = 2000; // 2 seconds between scans

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initViews();
        setupBarcodeScanner();
        textRecognizer = new TextRecognitionHelper();
        startCamera();
    }

    private void initViews() {
        previewView = findViewById(R.id.previewView);
        tvScanInstruction = findViewById(R.id.tvScanInstruction);
        btnManualEntry = findViewById(R.id.btnManualEntry);
        progressIndicator = findViewById(R.id.progressIndicator);
        MaterialButton btnScanLabel = findViewById(R.id.btnScanLabel);
        btnScanLabel.setOnClickListener(v -> {
            Intent intent = new Intent(ScanActivity.this, ImageIngredient.class);
            startActivity(intent);
        });
        btnManualEntry.setOnClickListener(v -> showManualEntryDialog());
        btnToggleMode = findViewById(R.id.btnToggleMode);
        tvModeIndicator = findViewById(R.id.tvModeIndicator);

        btnToggleMode.setOnClickListener(v -> toggleScanMode());
    }
    private void toggleScanMode() {
        isOcrMode = !isOcrMode;

        if (isOcrMode) {
            tvModeIndicator.setText("📄 Label Scanning Mode");
            tvScanInstruction.setText("Point camera at nutrition label\nTap screen to capture");
            btnToggleMode.setText("Switch to Barcode");

            // Add tap-to-capture
            previewView.setOnClickListener(v -> captureLabel());
        } else {
            tvModeIndicator.setText("📊 Barcode Scanning Mode");
            tvScanInstruction.setText("Align barcode within frame");
            btnToggleMode.setText("Switch to Label");
            previewView.setOnClickListener(null);
        }
    }

    private void captureLabel() {
        if (isProcessing) return;

        isProcessing = true;
        progressIndicator.setVisibility(View.VISIBLE);
        tvScanInstruction.setText("📸 Capturing label...");

        // Note: For now, this is a placeholder for actual frame processing implementation.
        // Actual implementation would need to capture the current frame and send it to textRecognizer.
        // Since the current frame handling is complex in CameraX, 
        // a simpler way would be to get the next frame from the analyzer.
    }
    
    private void setupBarcodeScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_QR_CODE)
                .build();

        barcodeScanner = BarcodeScanning.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Error starting camera: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            Log.d(TAG, "Camera bound successfully");
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
            Toast.makeText(this, "Camera binding failed", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void analyzeImage(ImageProxy imageProxy) {
        if (!isScanning || isProcessing) {
            imageProxy.close();
            return;
        }

        if (isOcrMode) {
            // Label scanning logic could go here if it was automated
            imageProxy.close();
            return;
        }

        // Check cooldown period
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime < SCAN_COOLDOWN) {
            imageProxy.close();
            return;
        }

        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        barcodeScanner.process(image)
                .addOnSuccessListener(this::processBarcodes)
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void processBarcodes(List<Barcode> barcodes) {
        if (!isScanning || isProcessing || barcodes.isEmpty()) {
            return;
        }

        for (Barcode barcode : barcodes) {
            String rawValue = barcode.getRawValue();
            if (rawValue != null && !rawValue.isEmpty() && rawValue.length() >= 8) {
                lastScanTime = System.currentTimeMillis();
                onBarcodeDetected(rawValue);
                break;
            }
        }
    }

    private void onBarcodeDetected(String barcode) {
        if (isProcessing) return;

        isProcessing = true;
        isScanning = false;

        runOnUiThread(() -> {
            progressIndicator.setVisibility(View.VISIBLE);
            tvScanInstruction.setText("✓ Barcode detected!\n" + barcode + "\n\nLoading product details...");
            btnManualEntry.setEnabled(false);

            // Add delay before navigation to show feedback
            previewView.postDelayed(() -> {
                fetchProductDetails(barcode);
            }, 500);
        });
    }

    private void fetchProductDetails(String barcode) {
        Log.d(TAG, "Fetching details for barcode: " + barcode);

        // Navigate to ProductDetailsActivity
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("BARCODE", barcode);
        startActivity(intent);

        // Don't finish immediately - let user see the transition
        previewView.postDelayed(this::finish, 300);
    }

    private void showManualEntryDialog() {
        // PAUSE CAMERA
        isScanning = false;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Barcode Manually");

        // Create a custom view with nice background
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_manual_entry, null);
        EditText input = dialogView.findViewById(R.id.etBarcodeInput);

        builder.setView(dialogView);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String barcode = input.getText().toString().trim();
            if (!barcode.isEmpty() && barcode.length() >= 8) {
                onBarcodeDetected(barcode);
            } else {
                Toast.makeText(this, "Please enter a valid barcode (at least 8 digits)",
                        Toast.LENGTH_LONG).show();
                // RESUME CAMERA
                startCamera();
                isScanning = true;
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            // RESUME CAMERA
            startCamera();
            isScanning = true;
        });

        builder.setOnCancelListener(dialog -> {
            // RESUME CAMERA
            startCamera();
            isScanning = true;
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScanning = true;
        isProcessing = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isScanning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
            cameraExecutor.shutdown();
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}