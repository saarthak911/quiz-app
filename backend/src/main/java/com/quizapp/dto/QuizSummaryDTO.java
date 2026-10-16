package com.quizapp.dto;

public class QuizSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private int questionCount;

    public QuizSummaryDTO(Long id, String title, String description, Integer durationMinutes, int questionCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.questionCount = questionCount;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public int getQuestionCount() { return questionCount; }
}
