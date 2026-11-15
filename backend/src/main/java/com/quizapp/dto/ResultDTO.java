package com.quizapp.dto;

import java.util.List;

public class ResultDTO {
    private Long attemptId;
    private int score;
    private int totalQuestions;
    private List<AnswerReviewDTO> review;
    // Students scoring below this percentage get the admin's suggested videos.
    private int suggestionThresholdPercent;
    // Empty when the student scored at/above the threshold or no videos were added.
    private List<ResourceDTO> suggestions;

    public ResultDTO(Long attemptId, int score, int totalQuestions, List<AnswerReviewDTO> review,
                     int suggestionThresholdPercent, List<ResourceDTO> suggestions) {
        this.attemptId = attemptId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.review = review;
        this.suggestionThresholdPercent = suggestionThresholdPercent;
        this.suggestions = suggestions;
    }

    public Long getAttemptId() { return attemptId; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public List<AnswerReviewDTO> getReview() { return review; }
    public int getSuggestionThresholdPercent() { return suggestionThresholdPercent; }
    public List<ResourceDTO> getSuggestions() { return suggestions; }
}
