package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class DietEntry {
    private String id;
    private String userId;
    private String productName;
    private String barcode;
    private double protein;
    private double carbohydrates;
    private double fats;
    private double calories;
    private double sodium;
    private double fiber;
    private long timestamp;
    private String date; // Format: "2026-02-20"

    public DietEntry() {
        // Required for Firestore
    }

    public DietEntry(String userId, String productName, String barcode,
                     NutritionalInfo nutrition) {
        this.userId = userId;
        this.productName = productName;
        this.barcode = barcode;
        this.protein = nutrition.getProtein();
        this.carbohydrates = nutrition.getCarbohydrates();
        this.fats = nutrition.getFat();
        this.calories = nutrition.getCalories();
        this.sodium = nutrition.getSodium() * 1000; // Convert to mg
        this.fiber = nutrition.getFiber();
        this.timestamp = System.currentTimeMillis();
        this.date = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.US).format(new java.util.Date());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("productName", productName);
        map.put("barcode", barcode);
        map.put("protein", protein);
        map.put("carbohydrates", carbohydrates);
        map.put("fats", fats);
        map.put("calories", calories);
        map.put("sodium", sodium);
        map.put("fiber", fiber);
        map.put("timestamp", timestamp);
        map.put("date", date);
        return map;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public double getFats() { return fats; }
    public void setFats(double fats) { this.fats = fats; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }

    public double getFiber() { return fiber; }
    public void setFiber(double fiber) { this.fiber = fiber; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}