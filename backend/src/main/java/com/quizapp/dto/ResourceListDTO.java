package com.quizapp.dto;

import java.util.List;

public class ResourceListDTO {
    private int thresholdPercent;
    private List<ResourceDTO> resources;

    public ResourceListDTO(int thresholdPercent, List<ResourceDTO> resources) {
        this.thresholdPercent = thresholdPercent;
        this.resources = resources;
    }

    public int getThresholdPercent() { return thresholdPercent; }
    public List<ResourceDTO> getResources() { return resources; }
}
