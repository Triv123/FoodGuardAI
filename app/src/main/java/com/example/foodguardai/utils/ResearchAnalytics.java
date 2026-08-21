package com.example.foodguardai.utils;

import com.example.foodguardai.logging.TestLog;

import java.util.List;

public class ResearchAnalytics {

    public static class AccuracyResult {
        public double accuracyPercent;
        public int correctCount;
        public int totalCount;

        public AccuracyResult(int correct, int total) {
            this.correctCount = correct;
            this.totalCount = total;
            this.accuracyPercent = total > 0 ? (correct * 100.0 / total) : 0;
        }
    }

    public static class LatencyStats {
        public double meanMs;
        public double medianMs;
        public double minMs;
        public double maxMs;
        public double stdDevMs;

        public LatencyStats(double mean, double median, double min, double max, double stdDev) {
            this.meanMs = mean;
            this.medianMs = median;
            this.minMs = min;
            this.maxMs = max;
            this.stdDevMs = stdDev;
        }
    }

    public static class RetrievalResult {
        public double retrievalRatePercent;
        public int foundCount;
        public int attemptCount;

        public RetrievalResult(int found, int attempts) {
            this.foundCount = found;
            this.attemptCount = attempts;
            this.retrievalRatePercent = attempts > 0 ? (found * 100.0 / attempts) : 0;
        }
    }

    public static class CompletionResult {
        public double completionRatePercent;
        public int completedCount;
        public int totalCount;

        public CompletionResult(int completed, int total) {
            this.completedCount = completed;
            this.totalCount = total;
            this.completionRatePercent = total > 0 ? (completed * 100.0 / total) : 0;
        }
    }

    public static class SusStats {
        public double meanScore;
        public double medianScore;
        public double minScore;
        public double maxScore;
        public double stdDev;
        public double confidenceInterval95Lower;
        public double confidenceInterval95Upper;
        public int participantCount;

        public SusStats(double mean, double median, double min, double max,
                        double stdDev, double ciLower, double ciUpper, int count) {
            this.meanScore = mean;
            this.medianScore = median;
            this.minScore = min;
            this.maxScore = max;
            this.stdDev = stdDev;
            this.confidenceInterval95Lower = ciLower;
            this.confidenceInterval95Upper = ciUpper;
            this.participantCount = count;
        }
    }

    public static AccuracyResult computeBarcodeAccuracy(List<TestLog> logs) {
        int correct = 0;
        int total = 0;
        for (TestLog log : logs) {
            if ("barcode".equals(log.getFeature()) && log.getBarcodeValue() != null) {
                total++;
                if (log.isBarcodeDecoded() && log.isProductRetrieved()) {
                    correct++;
                }
            }
        }
        return new AccuracyResult(correct, total);
    }

    public static RetrievalResult computeProductRetrievalRate(List<TestLog> logs) {
        int found = 0;
        int attempts = 0;
        for (TestLog log : logs) {
            if ("barcode".equals(log.getFeature()) && log.isBarcodeDecoded()) {
                attempts++;
                if (log.isProductRetrieved()) {
                    found++;
                }
            }
        }
        return new RetrievalResult(found, attempts);
    }

    public static CompletionResult computeOcrCompletion(List<TestLog> logs) {
        int completed = 0;
        int total = 0;
        for (TestLog log : logs) {
            if ("ocr_fields".equals(log.getFeature())) {
                total++;
                if (log.isOcrExtracted()) {
                    completed++;
                }
            }
        }
        return new CompletionResult(completed, total);
    }

    public static AccuracyResult computeOcrFieldAccuracy(List<TestLog> logs, String field) {
        int correct = 0;
        int total = 0;
        for (TestLog log : logs) {
            if (!"ocr_fields".equals(log.getFeature())) continue;
            total++;
            boolean extracted = false;
            switch (field) {
                case "protein": extracted = log.isProteinExtracted(); break;
                case "carbs": extracted = log.isCarbsExtracted(); break;
                case "fat": extracted = log.isFatExtracted(); break;
                case "sodium": extracted = log.isSodiumExtracted(); break;
                case "sugar": extracted = log.isSugarExtracted(); break;
                case "saturatedFat": extracted = log.isSaturatedFatExtracted(); break;
                case "fiber": extracted = log.isFiberExtracted(); break;
            }
            if (extracted) correct++;
        }
        return new AccuracyResult(correct, total);
    }

    public static LatencyStats computeLatencyStats(List<TestLog> logs) {
        return computeLatencyStatsForFeature(logs, null);
    }

    public static LatencyStats computeLatencyStatsForFeature(List<TestLog> logs, String feature) {
        java.util.List<Long> latencies = new java.util.ArrayList<>();
        for (TestLog log : logs) {
            if (feature != null && !feature.equals(log.getFeature())) continue;
            if (log.getLatencyMs() > 0) {
                latencies.add(log.getLatencyMs());
            }
        }
        if (latencies.isEmpty()) {
            return new LatencyStats(0, 0, 0, 0, 0);
        }
        java.util.Collections.sort(latencies);
        int n = latencies.size();
        double sum = 0;
        for (long l : latencies) sum += l;
        double mean = sum / n;
        double median = (n % 2 == 0)
                ? (latencies.get(n / 2 - 1) + latencies.get(n / 2)) / 2.0
                : latencies.get(n / 2);
        double min = latencies.get(0);
        double max = latencies.get(n - 1);
        double varianceSum = 0;
        for (long l : latencies) varianceSum += (l - mean) * (l - mean);
        double stdDev = Math.sqrt(varianceSum / n);
        return new LatencyStats(mean, median, min, max, stdDev);
    }

    public static CompletionResult computeVoiceCommandCompletion(List<TestLog> logs) {
        int completed = 0;
        int total = 0;
        for (TestLog log : logs) {
            if ("voice".equals(log.getFeature())) {
                total++;
                if (log.isSuccess()) {
                    completed++;
                }
            }
        }
        return new CompletionResult(completed, total);
    }

    public static AccuracyResult computeAiCompletion(List<TestLog> logs) {
        int completed = 0;
        int total = 0;
        for (TestLog log : logs) {
            if ("ai".equals(log.getFeature())) {
                total++;
                if (log.isSuccess()) {
                    completed++;
                }
            }
        }
        return new AccuracyResult(completed, total);
    }

    public static SusStats computeSusStats(List<com.example.foodguardai.models.SusResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return new SusStats(0, 0, 0, 0, 0, 0, 0, 0);
        }
        java.util.List<Double> scores = new java.util.ArrayList<>();
        for (com.example.foodguardai.models.SusResponse r : responses) {
            scores.add(r.getTotalScore());
        }
        java.util.Collections.sort(scores);
        int n = scores.size();
        double sum = 0;
        for (double s : scores) sum += s;
        double mean = sum / n;
        double median = (n % 2 == 0)
                ? (scores.get(n / 2 - 1) + scores.get(n / 2)) / 2.0
                : scores.get(n / 2);
        double min = scores.get(0);
        double max = scores.get(n - 1);
        double varianceSum = 0;
        for (double s : scores) varianceSum += (s - mean) * (s - mean);
        double stdDev = Math.sqrt(varianceSum / n);
        double se = stdDev / Math.sqrt(n);
        double ciLower = mean - 1.96 * se;
        double ciUpper = mean + 1.96 * se;
        return new SusStats(mean, median, min, max, stdDev, ciLower, ciUpper, n);
    }

    public static double computeMeanAbsDifference(List<com.example.foodguardai.models.AlgorithmScore> scores) {
        if (scores == null || scores.isEmpty()) return 0;
        double sum = 0;
        for (com.example.foodguardai.models.AlgorithmScore s : scores) {
            sum += s.getAbsoluteDifference();
        }
        return sum / scores.size();
    }

    public static double computeHallucinationRate(List<com.example.foodguardai.models.AlgorithmScore> scores) {
        if (scores == null || scores.isEmpty()) return 0;
        int flagged = 0;
        for (com.example.foodguardai.models.AlgorithmScore s : scores) {
            if (s.isHallucinationFlag()) flagged++;
        }
        return flagged * 100.0 / scores.size();
    }
}
