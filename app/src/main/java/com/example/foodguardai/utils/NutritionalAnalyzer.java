package com.example.foodguardai.utils;


import com.example.foodguardai.models.NutritionalInfo;

public class NutritionalAnalyzer {

    public static void analyzeNutrients(NutritionalInfo info) {
        // Analyze Protein (per 100g)
        info.setProteinLevel(analyzeProtein(info.getProtein()));

        // Analyze Carbohydrates (per 100g)
        info.setCarbLevel(analyzeCarbohydrates(info.getCarbohydrates()));

        // Analyze Sugar (per 100g)
        info.setSugarLevel(analyzeSugar(info.getSugar()));

        // Analyze Sodium (per 100g, in grams - convert to mg)
        info.setSodiumLevel(analyzeSodium(info.getSodium() * 1000));
    }

    private static String analyzeProtein(double protein) {
        if (protein < 3) return "Low - Consider protein-rich alternatives";
        if (protein < 10) return "Adequate - Provides moderate protein";
        return "High - Excellent protein source";
    }

    private static String analyzeCarbohydrates(double carbs) {
        if (carbs < 10) return "Low - Low carb product";
        if (carbs < 30) return "Moderate - Balanced carbohydrate content";
        return "High - High carbohydrate content";
    }

    private static String analyzeSugar(double sugar) {
        if (sugar < 5) return "Low - Low sugar content ✓";
        if (sugar < 15) return "Moderate - Moderate sugar levels";
        return "High - Excessive sugar ⚠️";
    }

    private static String analyzeSodium(double sodiumMg) {
        if (sodiumMg < 120) return "Low - Low sodium ✓";
        if (sodiumMg < 400) return "Moderate - Moderate sodium levels";
        return "High - High sodium content ⚠️";
    }
}