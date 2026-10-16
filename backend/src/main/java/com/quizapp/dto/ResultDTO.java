package com.quizapp.dto;

import java.util.List;

public class ResultDTO {
    private Long attemptId;
    private int score;
    private int totalQuestions;
    private List<AnswerReviewDTO> review;

    public ResultDTO(Long attemptId, int score, int totalQuestions, List<AnswerReviewDTO> review) {
        this.attemptId = attemptId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.review = review;
    }

    public Long getAttemptId() { return attemptId; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public List<AnswerReviewDTO> getReview() { return review; }
}
