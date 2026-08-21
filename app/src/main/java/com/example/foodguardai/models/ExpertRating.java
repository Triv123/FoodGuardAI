package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class ExpertRating {
    private String id;
    private String productId;
    private String barcode;
    private String productName;
    private String expertId;
    private int safetyRating;
    private int accuracyRating;
    private boolean hallucinationDetected;
    private String hallucinationDetails;
    private String contextCondition;
    private String notes;
    private long timestamp;

    public ExpertRating() {
        this.timestamp = System.currentTimeMillis();
    }

    public ExpertRating(String productId, String expertId, int safetyRating) {
        this.productId = productId;
        this.expertId = expertId;
        this.safetyRating = safetyRating;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getExpertId() { return expertId; }
    public void setExpertId(String expertId) { this.expertId = expertId; }

    public int getSafetyRating() { return safetyRating; }
    public void setSafetyRating(int safetyRating) { this.safetyRating = safetyRating; }

    public int getAccuracyRating() { return accuracyRating; }
    public void setAccuracyRating(int accuracyRating) { this.accuracyRating = accuracyRating; }

    public boolean isHallucinationDetected() { return hallucinationDetected; }
    public void setHallucinationDetected(boolean hallucinationDetected) { this.hallucinationDetected = hallucinationDetected; }

    public String getHallucinationDetails() { return hallucinationDetails; }
    public void setHallucinationDetails(String hallucinationDetails) { this.hallucinationDetails = hallucinationDetails; }

    public String getContextCondition() { return contextCondition; }
    public void setContextCondition(String contextCondition) { this.contextCondition = contextCondition; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("barcode", barcode);
        map.put("productName", productName);
        map.put("expertId", expertId);
        map.put("safetyRating", safetyRating);
        map.put("accuracyRating", accuracyRating);
        map.put("hallucinationDetected", hallucinationDetected);
        map.put("hallucinationDetails", hallucinationDetails);
        map.put("contextCondition", contextCondition);
        map.put("notes", notes);
        map.put("timestamp", timestamp);
        return map;
    }
}
