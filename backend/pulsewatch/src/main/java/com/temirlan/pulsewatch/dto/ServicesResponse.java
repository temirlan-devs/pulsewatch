package com.temirlan.pulsewatch.dto;

import java.util.List;

public class ServicesResponse {
    
    private List<String> services;

    public ServicesResponse(List<String> services) {
        this.services = services;
    }

    public List<String> getServices() {
        return services;
    }

}
