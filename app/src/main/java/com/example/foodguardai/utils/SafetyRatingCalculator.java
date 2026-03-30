package com.example.foodguardai.utils;

import com.example.foodguardai.models.NutritionalInfo;
import com.example.foodguardai.models.Product;

public class SafetyRatingCalculator {

    public static int calculateSafetyRating(Product product) {
        int rating = 10;

        // ── Instant 1 for known-banned product ──
        if (product.isBanned()) return 1;

        // ── Ingredient-based deductions (NEW) ──
        if (product.getIngredientsText() != null && !product.getIngredientsText().isEmpty()) {
            BannedProductChecker.IngredientAnalysisResult analysis =
                    BannedProductChecker.analyzeIngredients(product.getIngredientsText());

            rating -= analysis.safetyDeduction;

            // Hard floor of 1 if banned ingredients found
            if (analysis.hasBannedIngredients) {
                return Math.max(1, rating);
            }
        }

        // ── Nutrition-based deductions ──
        NutritionalInfo nutrition = product.getNutritionalInfo();
        if (nutrition == null) {
            return Math.max(1, Math.min(10, rating));
        }

        // Sugar (per 100g)
        if (nutrition.getSugar() > 15) rating -= 2;
        else if (nutrition.getSugar() > 10) rating -= 1;

        // Sodium (per 100g, stored in grams → convert to mg)
        double sodiumMg = nutrition.getSodium() * 1000;
        if (sodiumMg > 500) rating -= 2;
        else if (sodiumMg > 300) rating -= 1;

        // Saturated fat (per 100g)
        if (nutrition.getSaturatedFat() > 5) rating -= 2;
        else if (nutrition.getSaturatedFat() > 3) rating -= 1;

        // Calories (per 100g)
        if (nutrition.getCalories() > 500) rating -= 1;

        // ── Bonuses ──
        if (nutrition.getProtein() > 10) rating += 1;
        if (nutrition.getFiber() > 5) rating += 1;

        return Math.max(1, Math.min(10, rating));
    }

    public static String getRatingDescription(int rating) {
        if (rating >= 9) return "Excellent — Very Safe ✅";
        if (rating >= 7) return "Good — Generally Safe 👍";
        if (rating >= 5) return "Moderate — Consume with Caution ⚠️";
        if (rating >= 3) return "Poor — Limit Consumption 🔴";
        return "Very Poor — Avoid ❌";
    }

    /** Returns a color resource name based on rating for UI tinting */
    public static String getRatingColor(int rating) {
        if (rating >= 8) return "safe_green";
        if (rating >= 6) return "caution_yellow";
        if (rating >= 4) return "warning_orange";
        return "danger_red";
    }
}