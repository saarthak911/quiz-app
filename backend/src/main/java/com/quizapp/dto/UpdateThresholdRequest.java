package com.quizapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateThresholdRequest {
    @NotNull
    @Min(0)
    @Max(100)
    private Integer percent;

    public Integer getPercent() { return percent; }
    public void setPercent(Integer percent) { this.percent = percent; }
}
