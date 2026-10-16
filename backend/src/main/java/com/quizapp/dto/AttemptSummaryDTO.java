package com.quizapp.dto;

import java.time.LocalDateTime;

public class AttemptSummaryDTO {
    private Long attemptId;
    private String username;
    private Integer score;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    public AttemptSummaryDTO(Long attemptId, String username, Integer score, LocalDateTime startedAt, LocalDateTime submittedAt) {
        this.attemptId = attemptId;
        this.username = username;
        this.score = score;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
    }

    public Long getAttemptId() { return attemptId; }
    public String getUsername() { return username; }
    public Integer getScore() { return score; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}
