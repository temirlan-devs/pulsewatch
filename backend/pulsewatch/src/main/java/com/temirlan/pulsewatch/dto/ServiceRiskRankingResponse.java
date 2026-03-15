package com.temirlan.pulsewatch.dto;

public class ServiceRiskRankingResponse {
    
    private String service;
    private double riskScore;
    private String predictedStatus;
    private double confidence;

    public ServiceRiskRankingResponse (String service, double riskScore, String predictedStatus, double confidence) {
        this.service = service;
        this.riskScore = riskScore;
        this.predictedStatus = predictedStatus;
        this.confidence = confidence;
    }

    public String getService() {
        return service;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getPredictedStatus() {
        return predictedStatus;
    }

    public double getConfidence() {
        return confidence;
    }

}
