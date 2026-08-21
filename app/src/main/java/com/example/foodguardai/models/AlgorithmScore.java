package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmScore {
    private String id;
    private String productId;
    private String barcode;
    private String productName;
    private int algorithmSafetyRating;
    private int expertSafetyRating;
    private int absoluteDifference;
    private boolean hallucinationFlag;
    private String contextCondition;
    private String scoringVersion;
    private long timestamp;

    public AlgorithmScore() {
        this.timestamp = System.currentTimeMillis();
    }

    public AlgorithmScore(String productId, int algorithmRating, int expertRating) {
        this.productId = productId;
        this.algorithmSafetyRating = algorithmRating;
        this.expertSafetyRating = expertRating;
        this.absoluteDifference = Math.abs(algorithmRating - expertRating);
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

    public int getAlgorithmSafetyRating() { return algorithmSafetyRating; }
    public void setAlgorithmSafetyRating(int algorithmSafetyRating) { this.algorithmSafetyRating = algorithmSafetyRating; }

    public int getExpertSafetyRating() { return expertSafetyRating; }
    public void setExpertSafetyRating(int expertSafetyRating) { this.expertSafetyRating = expertSafetyRating; }

    public int getAbsoluteDifference() { return absoluteDifference; }
    public void setAbsoluteDifference(int absoluteDifference) { this.absoluteDifference = absoluteDifference; }

    public boolean isHallucinationFlag() { return hallucinationFlag; }
    public void setHallucinationFlag(boolean hallucinationFlag) { this.hallucinationFlag = hallucinationFlag; }

    public String getContextCondition() { return contextCondition; }
    public void setContextCondition(String contextCondition) { this.contextCondition = contextCondition; }

    public String getScoringVersion() { return scoringVersion; }
    public void setScoringVersion(String scoringVersion) { this.scoringVersion = scoringVersion; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("barcode", barcode);
        map.put("productName", productName);
        map.put("algorithmSafetyRating", algorithmSafetyRating);
        map.put("expertSafetyRating", expertSafetyRating);
        map.put("absoluteDifference", absoluteDifference);
        map.put("hallucinationFlag", hallucinationFlag);
        map.put("contextCondition", contextCondition);
        map.put("scoringVersion", scoringVersion);
        map.put("timestamp", timestamp);
        return map;
    }
}
