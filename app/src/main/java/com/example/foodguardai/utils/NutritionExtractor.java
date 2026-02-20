package com.example.foodguardai.utils;

import android.util.Log;
import com.example.foodguardai.models.NutritionalInfo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NutritionExtractor {
    private static final String TAG = "NutritionExtractor";

    public static class ExtractedNutrition {
        public String productName;
        public NutritionalInfo nutritionalInfo;
        public String ingredients;
        public boolean success;
        public String errorMessage;
    }

    public static ExtractedNutrition extractNutritionFromText(String text) {
        ExtractedNutrition result = new ExtractedNutrition();
        result.nutritionalInfo = new NutritionalInfo();

        try {
            // Extract product name (usually first line or largest text)
            String[] lines = text.split("\n");
            if (lines.length > 0) {
                result.productName = lines[0].trim();
            }

            // Extract protein
            result.nutritionalInfo.setProtein(extractNutrient(text,
                    "protein[s]?[:\\s]+([0-9.]+)\\s*g", "protein"));

            // Extract carbohydrates
            result.nutritionalInfo.setCarbohydrates(extractNutrient(text,
                    "carbohydrate[s]?[:\\s]+([0-9.]+)\\s*g", "carbs"));

            // Extract sugar
            result.nutritionalInfo.setSugar(extractNutrient(text,
                    "sugar[s]?[:\\s]+([0-9.]+)\\s*g", "sugar"));

            // Extract fat
            result.nutritionalInfo.setFat(extractNutrient(text,
                    "fat[:\\s]+([0-9.]+)\\s*g", "fat"));

            // Extract sodium
            double sodiumMg = extractNutrient(text,
                    "sodium[:\\s]+([0-9.]+)\\s*mg", "sodium");
            result.nutritionalInfo.setSodium(sodiumMg / 1000.0); // Convert to grams

            // Extract calories
            result.nutritionalInfo.setCalories(extractNutrient(text,
                    "calor(?:ies)?[:\\s]+([0-9.]+)", "calories"));

            // Extract fiber
            result.nutritionalInfo.setFiber(extractNutrient(text,
                    "fiber[:\\s]+([0-9.]+)\\s*g", "fiber"));

            result.success = true;

        } catch (Exception e) {
            Log.e(TAG, "Error extracting nutrition", e);
            result.success = false;
            result.errorMessage = e.getMessage();
        }

        return result;
    }

    private static double extractNutrient(String text, String regex, String nutrientName) {
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String value = matcher.group(1);
                double nutrientValue = Double.parseDouble(value);
                Log.d(TAG, "Found " + nutrientName + ": " + nutrientValue);
                return nutrientValue;
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not extract " + nutrientName, e);
        }
        return 0.0;
    }
}