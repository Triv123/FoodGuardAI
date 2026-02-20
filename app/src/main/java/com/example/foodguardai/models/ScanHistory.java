package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class ScanHistory {
    private String id;
    private String userId;
    private String barcode;
    private String productName;
    private String brand;
    private int safetyRating;
    private long timestamp;
    private String scanType; // "barcode" or "label"

    public ScanHistory() {
        // Required for Firestore
    }

    public ScanHistory(String userId, String barcode, String productName,
                       String brand, int safetyRating, String scanType) {
        this.userId = userId;
        this.barcode = barcode;
        this.productName = productName;
        this.brand = brand;
        this.safetyRating = safetyRating;
        this.timestamp = System.currentTimeMillis();
        this.scanType = scanType;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("barcode", barcode);
        map.put("productName", productName);
        map.put("brand", brand);
        map.put("safetyRating", safetyRating);
        map.put("timestamp", timestamp);
        map.put("scanType", scanType);
        return map;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getSafetyRating() { return safetyRating; }
    public void setSafetyRating(int safetyRating) { this.safetyRating = safetyRating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
}