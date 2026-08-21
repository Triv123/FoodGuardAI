package com.example.foodguardai.logging;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestLogManager {
    private static final String TAG = "TestLogManager";
    private static TestLogManager instance;

    private final List<TestLog> logs;
    private boolean enabled = true;

    private TestLogManager() {
        logs = Collections.synchronizedList(new ArrayList<TestLog>());
    }

    public static synchronized TestLogManager getInstance() {
        if (instance == null) {
            instance = new TestLogManager();
        }
        return instance;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void log(TestLog log) {
        if (!enabled || log == null) return;
        if (log.getId() == null) {
            log.setId(UUID.randomUUID().toString());
        }
        if (log.getTimestamp() == 0) {
            log.setTimestamp(System.currentTimeMillis());
        }
        logs.add(log);
        Log.d(TAG, "Logged [" + log.getFeature() + "] success=" + log.isSuccess()
                + " latency=" + log.getLatencyMs() + "ms");
    }

    public List<TestLog> getAllLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }

    public List<TestLog> getLogsByFeature(String feature) {
        List<TestLog> filtered = new ArrayList<>();
        synchronized (logs) {
            for (TestLog log : logs) {
                if (feature.equals(log.getFeature())) {
                    filtered.add(log);
                }
            }
        }
        return filtered;
    }

    public void clearLogs() {
        logs.clear();
        Log.d(TAG, "All logs cleared");
    }

    public int getLogCount() {
        return logs.size();
    }

    public TestLog createBarcodeLog(String barcode, boolean decoded, boolean productFound,
                                     long latencyMs, String rawOutput) {
        TestLog log = new TestLog("barcode");
        log.setBarcodeValue(barcode);
        log.setBarcodeDecoded(decoded);
        log.setProductRetrieved(productFound);
        log.setLatencyMs(latencyMs);
        log.setRawOutput(rawOutput);
        log.setSuccess(decoded && productFound);
        return log;
    }

    public TestLog createOcrLog(boolean extracted, long latencyMs, String rawOutput) {
        TestLog log = new TestLog("ocr");
        log.setOcrExtracted(extracted);
        log.setLatencyMs(latencyMs);
        log.setRawOutput(rawOutput);
        log.setSuccess(extracted);
        return log;
    }

    public TestLog createOcrFieldLog(boolean proteinOk, Double proteinVal,
                                      boolean carbsOk, Double carbsVal,
                                      boolean fatOk, Double fatVal,
                                      boolean sodiumOk, Double sodiumVal,
                                      boolean sugarOk, Double sugarVal,
                                      boolean satFatOk, Double satFatVal,
                                      boolean fiberOk, Double fiberVal,
                                      long latencyMs) {
        TestLog log = new TestLog("ocr_fields");
        log.setProteinExtracted(proteinOk);
        log.setProteinValue(proteinVal);
        log.setCarbsExtracted(carbsOk);
        log.setCarbsValue(carbsVal);
        log.setFatExtracted(fatOk);
        log.setFatValue(fatVal);
        log.setSodiumExtracted(sodiumOk);
        log.setSodiumValue(sodiumVal);
        log.setSugarExtracted(sugarOk);
        log.setSugarValue(sugarVal);
        log.setSaturatedFatExtracted(satFatOk);
        log.setSaturatedFatValue(satFatVal);
        log.setFiberExtracted(fiberOk);
        log.setFiberValue(fiberVal);
        log.setLatencyMs(latencyMs);
        boolean allExtracted = proteinOk && carbsOk && fatOk && sodiumOk && sugarOk && satFatOk && fiberOk;
        log.setSuccess(allExtracted);
        return log;
    }

    public TestLog createVoiceLog(String intendedCommand, String transcription,
                                   String matchedCommand, String executedAction,
                                   boolean success, long latencyMs) {
        TestLog log = new TestLog("voice");
        log.setIntendedCommand(intendedCommand);
        log.setRecognizedTranscription(transcription);
        log.setMatchedCommand(matchedCommand);
        log.setExecutedAction(executedAction);
        log.setSuccess(success);
        log.setLatencyMs(latencyMs);
        return log;
    }

    public TestLog createAiLog(String status, long latencyMs, String contextMode,
                                String productContext, String dietContext,
                                String convContext, String response, String model) {
        TestLog log = new TestLog("ai");
        log.setRequestStatus(status);
        log.setLatencyMs(latencyMs);
        log.setContextMode(contextMode);
        log.setProductContext(productContext);
        log.setDietHistoryContext(dietContext);
        log.setConversationContext(convContext);
        log.setResponseOutput(response);
        log.setAiModelUsed(model);
        log.setSuccess("success".equalsIgnoreCase(status));
        return log;
    }
}
