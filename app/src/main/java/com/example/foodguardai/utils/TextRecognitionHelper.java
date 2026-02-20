package com.example.foodguardai.utils;

import android.graphics.Rect;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class TextRecognitionHelper {
    private static final String TAG = "TextRecognition";
    private final TextRecognizer recognizer;

    public interface TextRecognitionCallback {
        void onSuccess(String extractedText);
        void onFailure(String error);
    }

    public TextRecognitionHelper() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void recognizeText(InputImage image, TextRecognitionCallback callback) {
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String extractedText = visionText.getText();
                    Log.d(TAG, "Extracted text: " + extractedText);

                    if (extractedText.isEmpty()) {
                        callback.onFailure("No text found in image");
                    } else {
                        callback.onSuccess(extractedText);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void close() {
        if (recognizer != null) {
            recognizer.close();
        }
    }
}