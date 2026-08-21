package com.example.foodguardai.logging;

import java.util.HashMap;
import java.util.Map;

public class TestLog {
    private String id;
    private long timestamp;
    private String feature;
    private String inputConditions;
    private boolean success;
    private long latencyMs;
    private String rawOutput;

    private boolean barcodeDecoded;
    private String barcodeValue;
    private boolean productRetrieved;
    private String productName;

    private boolean ocrExtracted;
    private boolean proteinExtracted;
    private boolean carbsExtracted;
    private boolean fatExtracted;
    private boolean sodiumExtracted;
    private boolean sugarExtracted;
    private boolean saturatedFatExtracted;
    private boolean fiberExtracted;
    private Double proteinValue;
    private Double carbsValue;
    private Double fatValue;
    private Double sodiumValue;
    private Double sugarValue;
    private Double saturatedFatValue;
    private Double fiberValue;

    private String lightingCondition;
    private String captureAngle;
    private String labelCondition;
    private String noiseLevel;

    private String intendedCommand;
    private String recognizedTranscription;
    private String matchedCommand;
    private String executedAction;

    private String requestStatus;
    private String contextMode;
    private String productContext;
    private String dietHistoryContext;
    private String conversationContext;
    private String responseOutput;
    private String aiModelUsed;

    public TestLog() {
        this.timestamp = System.currentTimeMillis();
    }

    public TestLog(String feature) {
        this.timestamp = System.currentTimeMillis();
        this.feature = feature;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public String getInputConditions() { return inputConditions; }
    public void setInputConditions(String inputConditions) { this.inputConditions = inputConditions; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

    public String getRawOutput() { return rawOutput; }
    public void setRawOutput(String rawOutput) { this.rawOutput = rawOutput; }

    public boolean isBarcodeDecoded() { return barcodeDecoded; }
    public void setBarcodeDecoded(boolean barcodeDecoded) { this.barcodeDecoded = barcodeDecoded; }

    public String getBarcodeValue() { return barcodeValue; }
    public void setBarcodeValue(String barcodeValue) { this.barcodeValue = barcodeValue; }

    public boolean isProductRetrieved() { return productRetrieved; }
    public void setProductRetrieved(boolean productRetrieved) { this.productRetrieved = productRetrieved; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public boolean isOcrExtracted() { return ocrExtracted; }
    public void setOcrExtracted(boolean ocrExtracted) { this.ocrExtracted = ocrExtracted; }

    public boolean isProteinExtracted() { return proteinExtracted; }
    public void setProteinExtracted(boolean proteinExtracted) { this.proteinExtracted = proteinExtracted; }

    public boolean isCarbsExtracted() { return carbsExtracted; }
    public void setCarbsExtracted(boolean carbsExtracted) { this.carbsExtracted = carbsExtracted; }

    public boolean isFatExtracted() { return fatExtracted; }
    public void setFatExtracted(boolean fatExtracted) { this.fatExtracted = fatExtracted; }

    public boolean isSodiumExtracted() { return sodiumExtracted; }
    public void setSodiumExtracted(boolean sodiumExtracted) { this.sodiumExtracted = sodiumExtracted; }

    public boolean isSugarExtracted() { return sugarExtracted; }
    public void setSugarExtracted(boolean sugarExtracted) { this.sugarExtracted = sugarExtracted; }

    public boolean isSaturatedFatExtracted() { return saturatedFatExtracted; }
    public void setSaturatedFatExtracted(boolean saturatedFatExtracted) { this.saturatedFatExtracted = saturatedFatExtracted; }

    public boolean isFiberExtracted() { return fiberExtracted; }
    public void setFiberExtracted(boolean fiberExtracted) { this.fiberExtracted = fiberExtracted; }

    public Double getProteinValue() { return proteinValue; }
    public void setProteinValue(Double proteinValue) { this.proteinValue = proteinValue; }

    public Double getCarbsValue() { return carbsValue; }
    public void setCarbsValue(Double carbsValue) { this.carbsValue = carbsValue; }

    public Double getFatValue() { return fatValue; }
    public void setFatValue(Double fatValue) { this.fatValue = fatValue; }

    public Double getSodiumValue() { return sodiumValue; }
    public void setSodiumValue(Double sodiumValue) { this.sodiumValue = sodiumValue; }

    public Double getSugarValue() { return sugarValue; }
    public void setSugarValue(Double sugarValue) { this.sugarValue = sugarValue; }

    public Double getSaturatedFatValue() { return saturatedFatValue; }
    public void setSaturatedFatValue(Double saturatedFatValue) { this.saturatedFatValue = saturatedFatValue; }

    public Double getFiberValue() { return fiberValue; }
    public void setFiberValue(Double fiberValue) { this.fiberValue = fiberValue; }

    public String getLightingCondition() { return lightingCondition; }
    public void setLightingCondition(String lightingCondition) { this.lightingCondition = lightingCondition; }

    public String getCaptureAngle() { return captureAngle; }
    public void setCaptureAngle(String captureAngle) { this.captureAngle = captureAngle; }

    public String getLabelCondition() { return labelCondition; }
    public void setLabelCondition(String labelCondition) { this.labelCondition = labelCondition; }

    public String getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(String noiseLevel) { this.noiseLevel = noiseLevel; }

    public String getIntendedCommand() { return intendedCommand; }
    public void setIntendedCommand(String intendedCommand) { this.intendedCommand = intendedCommand; }

    public String getRecognizedTranscription() { return recognizedTranscription; }
    public void setRecognizedTranscription(String recognizedTranscription) { this.recognizedTranscription = recognizedTranscription; }

    public String getMatchedCommand() { return matchedCommand; }
    public void setMatchedCommand(String matchedCommand) { this.matchedCommand = matchedCommand; }

    public String getExecutedAction() { return executedAction; }
    public void setExecutedAction(String executedAction) { this.executedAction = executedAction; }

    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

    public String getContextMode() { return contextMode; }
    public void setContextMode(String contextMode) { this.contextMode = contextMode; }

    public String getProductContext() { return productContext; }
    public void setProductContext(String productContext) { this.productContext = productContext; }

    public String getDietHistoryContext() { return dietHistoryContext; }
    public void setDietHistoryContext(String dietHistoryContext) { this.dietHistoryContext = dietHistoryContext; }

    public String getConversationContext() { return conversationContext; }
    public void setConversationContext(String conversationContext) { this.conversationContext = conversationContext; }

    public String getResponseOutput() { return responseOutput; }
    public void setResponseOutput(String responseOutput) { this.responseOutput = responseOutput; }

    public String getAiModelUsed() { return aiModelUsed; }
    public void setAiModelUsed(String aiModelUsed) { this.aiModelUsed = aiModelUsed; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("timestamp", timestamp);
        map.put("feature", feature);
        map.put("inputConditions", inputConditions);
        map.put("success", success);
        map.put("latencyMs", latencyMs);
        map.put("rawOutput", rawOutput);
        map.put("barcodeDecoded", barcodeDecoded);
        map.put("barcodeValue", barcodeValue);
        map.put("productRetrieved", productRetrieved);
        map.put("productName", productName);
        map.put("ocrExtracted", ocrExtracted);
        map.put("proteinExtracted", proteinExtracted);
        map.put("carbsExtracted", carbsExtracted);
        map.put("fatExtracted", fatExtracted);
        map.put("sodiumExtracted", sodiumExtracted);
        map.put("sugarExtracted", sugarExtracted);
        map.put("saturatedFatExtracted", saturatedFatExtracted);
        map.put("fiberExtracted", fiberExtracted);
        map.put("proteinValue", proteinValue);
        map.put("carbsValue", carbsValue);
        map.put("fatValue", fatValue);
        map.put("sodiumValue", sodiumValue);
        map.put("sugarValue", sugarValue);
        map.put("saturatedFatValue", saturatedFatValue);
        map.put("fiberValue", fiberValue);
        map.put("lightingCondition", lightingCondition);
        map.put("captureAngle", captureAngle);
        map.put("labelCondition", labelCondition);
        map.put("noiseLevel", noiseLevel);
        map.put("intendedCommand", intendedCommand);
        map.put("recognizedTranscription", recognizedTranscription);
        map.put("matchedCommand", matchedCommand);
        map.put("executedAction", executedAction);
        map.put("requestStatus", requestStatus);
        map.put("contextMode", contextMode);
        map.put("productContext", productContext);
        map.put("dietHistoryContext", dietHistoryContext);
        map.put("conversationContext", conversationContext);
        map.put("responseOutput", responseOutput);
        map.put("aiModelUsed", aiModelUsed);
        return map;
    }
}
