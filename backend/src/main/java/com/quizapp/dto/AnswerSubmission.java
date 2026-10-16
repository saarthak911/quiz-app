package com.quizapp.dto;

import jakarta.validation.constraints.NotNull;

public class AnswerSubmission {
    @NotNull
    private Long questionId;
    // Selected option can be null if the student left it blank.
    private String selectedOption;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}
