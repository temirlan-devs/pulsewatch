package com.temirlan.pulsewatch.dto;

public class ServiceInsightsResponse {
    
    private String service;
    private String summary;
    private String likelyCause;
    private String recommendedAction;
    private double riskScore;
    private String predictedStatus;

    public ServiceInsightsResponse(String service, String summary, String likelyCause, String recommendedAction, double riskScore, String predictedStatus) {
        this.service = service;
        this.summary = summary;
        this.likelyCause = likelyCause;
        this.recommendedAction = recommendedAction;
        this.riskScore = riskScore;
        this.predictedStatus = predictedStatus;
    }

    public String getService() {
        return service;
    }

    public String getSummary() {
        return summary;
    }

    public String getLikelyCause() {
        return likelyCause;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getPredictedStatus() {
        return predictedStatus;
    }

}
