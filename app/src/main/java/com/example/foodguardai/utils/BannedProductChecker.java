package com.example.foodguardai.utils;


import com.example.foodguardai.models.Product;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BannedProductChecker {

    // Sample banned ingredients/chemicals (expand this list based on FSSAI regulations)
    private static final Set<String> BANNED_INGREDIENTS = new HashSet<>(Arrays.asList(
            // Food colors banned in India
            "rhodamine b",
            "metanil yellow",
            "malachite green",
            "sudan red",
            "sudan i",
            "sudan ii",
            "sudan iii",
            "sudan iv",
            "orange ii",
            "carmoisine",

            // Preservatives and additives
            "potassium bromate",
            "brominated vegetable oil",
            "azodicarbonamide",
            "formaldehyde",
            "melamine",

            // Sweeteners (some banned/restricted)
            "cyclamate",
            "dulcin",

            // Others
            "boric acid",
            "borax",
            "diethylene glycol",
            "non-permitted coal tar dyes",

            // Trans fats (above certain limits)
            "partially hydrogenated oils",
            "vanaspati with high trans fat"
    ));

    // Banned products by category or brand
    private static final Set<String> BANNED_PRODUCTS = new HashSet<>(Arrays.asList(
            // Add specific product names that have been banned
            // Example format: "brand product variant year"
            "maggi noodles lead contamination 2015",
            "nestle india maggi",

            // Chinese products with melamine
            "chinese milk powder melamine",

            // Products with excessive pesticides
            // Add as they are identified by FSSAI

            // Note: This list should be updated regularly from FSSAI notifications
            "product banned by fssai"  // Template
    ));

    public static class BanStatus {
        private boolean isBanned;
        private String reason;

        public BanStatus(boolean isBanned, String reason) {
            this.isBanned = isBanned;
            this.reason = reason;
        }

        public boolean isBanned() {
            return isBanned;
        }

        public String getReason() {
            return reason;
        }
    }

    public static BanStatus checkIfBanned(Product product) {
        // Check product name
        String productName = product.getProductName() != null ?
                product.getProductName().toLowerCase() : "";

        for (String bannedProduct : BANNED_PRODUCTS) {
            if (productName.contains(bannedProduct)) {
                return new BanStatus(true, "Product banned by food authority");
            }
        }

        // Check ingredients (if available)
        // This would require ingredients data from API
        // For now, using placeholder logic

        // Check for harmful additives based on category
        if (containsHarmfulAdditives(product)) {
            return new BanStatus(true, "Contains banned ingredients");
        }

        return new BanStatus(false, "");
    }

    private static boolean containsHarmfulAdditives(Product product) {
        // Placeholder - in real implementation, check against ingredients list
        // You would parse ingredients from API response
        return false;
    }

    // Method to check specific ingredient
    public static boolean isIngredientBanned(String ingredient) {
        return BANNED_INGREDIENTS.contains(ingredient.toLowerCase().trim());
    }

    // Method to add banned products dynamically (for admin/update feature)
    public static void addBannedProduct(String productName) {
        BANNED_PRODUCTS.add(productName.toLowerCase());
    }

    // Method to add banned ingredient
    public static void addBannedIngredient(String ingredient) {
        BANNED_INGREDIENTS.add(ingredient.toLowerCase());
    }
}