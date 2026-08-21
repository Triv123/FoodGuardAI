package com.example.foodguardai.logging;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentConditions {
    private String lighting;
    private String captureAngle;
    private String labelCondition;
    private String noiseLevel;

    public EnvironmentConditions() {}

    public EnvironmentConditions(String lighting, String captureAngle,
                                  String labelCondition, String noiseLevel) {
        this.lighting = lighting;
        this.captureAngle = captureAngle;
        this.labelCondition = labelCondition;
        this.noiseLevel = noiseLevel;
    }

    public String getLighting() { return lighting; }
    public void setLighting(String lighting) { this.lighting = lighting; }

    public String getCaptureAngle() { return captureAngle; }
    public void setCaptureAngle(String captureAngle) { this.captureAngle = captureAngle; }

    public String getLabelCondition() { return labelCondition; }
    public void setLabelCondition(String labelCondition) { this.labelCondition = labelCondition; }

    public String getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(String noiseLevel) { this.noiseLevel = noiseLevel; }

    public void applyToLog(TestLog log) {
        if (log == null) return;
        log.setLightingCondition(lighting);
        log.setCaptureAngle(captureAngle);
        log.setLabelCondition(labelCondition);
        log.setNoiseLevel(noiseLevel);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("lighting", lighting);
        map.put("captureAngle", captureAngle);
        map.put("labelCondition", labelCondition);
        map.put("noiseLevel", noiseLevel);
        return map;
    }

    public static EnvironmentConditions fromMap(Map<String, Object> map) {
        EnvironmentConditions ec = new EnvironmentConditions();
        if (map == null) return ec;
        ec.lighting = (String) map.get("lighting");
        ec.captureAngle = (String) map.get("captureAngle");
        ec.labelCondition = (String) map.get("labelCondition");
        ec.noiseLevel = (String) map.get("noiseLevel");
        return ec;
    }
}
