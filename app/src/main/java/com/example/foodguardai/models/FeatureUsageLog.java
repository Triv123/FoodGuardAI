package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class FeatureUsageLog {
    private String id;
    private String participantId;
    private String featureName;
    private int usageCount;
    private long firstUsedAt;
    private long lastUsedAt;
    private long totalDurationMs;
    private String sessionId;

    public FeatureUsageLog() {
        this.firstUsedAt = System.currentTimeMillis();
        this.lastUsedAt = System.currentTimeMillis();
        this.usageCount = 0;
    }

    public FeatureUsageLog(String participantId, String featureName) {
        this.participantId = participantId;
        this.featureName = featureName;
        this.firstUsedAt = System.currentTimeMillis();
        this.lastUsedAt = System.currentTimeMillis();
        this.usageCount = 0;
    }

    public void recordUsage(long durationMs) {
        this.usageCount++;
        this.lastUsedAt = System.currentTimeMillis();
        this.totalDurationMs += durationMs;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }

    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public long getFirstUsedAt() { return firstUsedAt; }
    public void setFirstUsedAt(long firstUsedAt) { this.firstUsedAt = firstUsedAt; }

    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    public long getTotalDurationMs() { return totalDurationMs; }
    public void setTotalDurationMs(long totalDurationMs) { this.totalDurationMs = totalDurationMs; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("participantId", participantId);
        map.put("featureName", featureName);
        map.put("usageCount", usageCount);
        map.put("firstUsedAt", firstUsedAt);
        map.put("lastUsedAt", lastUsedAt);
        map.put("totalDurationMs", totalDurationMs);
        map.put("sessionId", sessionId);
        return map;
    }
}
