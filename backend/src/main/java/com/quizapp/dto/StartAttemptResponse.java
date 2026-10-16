package com.quizapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class StartAttemptResponse {
    private Long attemptId;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private List<QuestionForStudentDTO> questions;

    public StartAttemptResponse(Long attemptId, Integer durationMinutes, LocalDateTime startedAt, List<QuestionForStudentDTO> questions) {
        this.attemptId = attemptId;
        this.durationMinutes = durationMinutes;
        this.startedAt = startedAt;
        this.questions = questions;
    }

    public Long getAttemptId() { return attemptId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public List<QuestionForStudentDTO> getQuestions() { return questions; }
}
