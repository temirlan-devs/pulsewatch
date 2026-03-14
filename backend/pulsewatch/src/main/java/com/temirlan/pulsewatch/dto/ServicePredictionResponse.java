package com.temirlan.pulsewatch.dto;

public class ServicePredictionResponse {
    
    private String service;
    private String predictedStatus;
    private double riskScore;
    private double confidence;
    private String reason;

    public ServicePredictionResponse(String service, String predictedStatus, double riskScore, double confidence, String reason) {
        this.service = service;
        this.predictedStatus = predictedStatus;
        this.riskScore = riskScore;
        this.confidence = confidence;
        this.reason = reason;
    }

    public String getService() {
        return service;
    }

    public String getPredictedStatus() {
        return predictedStatus;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }

}
