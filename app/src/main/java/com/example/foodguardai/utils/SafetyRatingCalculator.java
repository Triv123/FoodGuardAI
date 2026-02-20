package com.example.foodguardai.utils;


import com.example.foodguardai.models.NutritionalInfo;
import com.example.foodguardai.models.Product;

public class SafetyRatingCalculator {

    public static int calculateSafetyRating(Product product) {
        int rating = 10; // Start with perfect score

        // Check if banned
        if (product.isBanned()) {
            return 1; // Minimum rating for banned products
        }

        NutritionalInfo nutrition = product.getNutritionalInfo();
        if (nutrition == null) {
            return 5; // Neutral if no info
        }

        // Deduct for high sugar
        if (nutrition.getSugar() > 15) { // High sugar per 100g
            rating -= 2;
        } else if (nutrition.getSugar() > 10) {
            rating -= 1;
        }

        // Deduct for high sodium
        double sodiumMg = nutrition.getSodium() * 1000; // Convert to mg
        if (sodiumMg > 500) { // High sodium per 100g
            rating -= 2;
        } else if (sodiumMg > 300) {
            rating -= 1;
        }

        // Deduct for high saturated fat
        if (nutrition.getSaturatedFat() > 5) { // High saturated fat per 100g
            rating -= 2;
        } else if (nutrition.getSaturatedFat() > 3) {
            rating -= 1;
        }

        // Add for good protein content
        if (nutrition.getProtein() > 10) {
            rating += 1;
        }

        // Add for good fiber content
        if (nutrition.getFiber() > 5) {
            rating += 1;
        }

        // Ensure rating is between 1-10
        return Math.max(1, Math.min(10, rating));
    }

    public static String getRatingDescription(int rating) {
        if (rating >= 9) return "Excellent - Very Safe";
        if (rating >= 7) return "Good - Generally Safe";
        if (rating >= 5) return "Moderate - Consume with Caution";
        if (rating >= 3) return "Poor - Limit Consumption";
        return "Very Poor - Avoid";
    }
}