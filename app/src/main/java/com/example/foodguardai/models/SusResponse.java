package com.example.foodguardai.models;

import java.util.HashMap;
import java.util.Map;

public class SusResponse {
    private String id;
    private String participantId;
    private int q1;
    private int q2;
    private int q3;
    private int q4;
    private int q5;
    private int q6;
    private int q7;
    private int q8;
    private int q9;
    private int q10;
    private double totalScore;
    private long timestamp;
    private String sessionId;

    public SusResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public SusResponse(String participantId, int q1, int q2, int q3, int q4, int q5,
                        int q6, int q7, int q8, int q9, int q10) {
        this.participantId = participantId;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q5 = q5;
        this.q6 = q6;
        this.q7 = q7;
        this.q8 = q8;
        this.q9 = q9;
        this.q10 = q10;
        this.timestamp = System.currentTimeMillis();
        this.totalScore = computeSusScore();
    }

    public double computeSusScore() {
        double score = 0;
        score += (q1 - 1) * 2.5;
        score += (5 - q2) * 2.5;
        score += (q3 - 1) * 2.5;
        score += (5 - q4) * 2.5;
        score += (q5 - 1) * 2.5;
        score += (5 - q6) * 2.5;
        score += (q7 - 1) * 2.5;
        score += (5 - q8) * 2.5;
        score += (q9 - 1) * 2.5;
        score += (5 - q10) * 2.5;
        return score;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }

    public int getQ1() { return q1; }
    public void setQ1(int q1) { this.q1 = q1; }

    public int getQ2() { return q2; }
    public void setQ2(int q2) { this.q2 = q2; }

    public int getQ3() { return q3; }
    public void setQ3(int q3) { this.q3 = q3; }

    public int getQ4() { return q4; }
    public void setQ4(int q4) { this.q4 = q4; }

    public int getQ5() { return q5; }
    public void setQ5(int q5) { this.q5 = q5; }

    public int getQ6() { return q6; }
    public void setQ6(int q6) { this.q6 = q6; }

    public int getQ7() { return q7; }
    public void setQ7(int q7) { this.q7 = q7; }

    public int getQ8() { return q8; }
    public void setQ8(int q8) { this.q8 = q8; }

    public int getQ9() { return q9; }
    public void setQ9(int q9) { this.q9 = q9; }

    public int getQ10() { return q10; }
    public void setQ10(int q10) { this.q10 = q10; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("participantId", participantId);
        map.put("q1", q1);
        map.put("q2", q2);
        map.put("q3", q3);
        map.put("q4", q4);
        map.put("q5", q5);
        map.put("q6", q6);
        map.put("q7", q7);
        map.put("q8", q8);
        map.put("q9", q9);
        map.put("q10", q10);
        map.put("totalScore", totalScore);
        map.put("timestamp", timestamp);
        map.put("sessionId", sessionId);
        return map;
    }
}
