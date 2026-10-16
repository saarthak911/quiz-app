package com.quizapp.dto;

public class AnswerReviewDTO {
    private Long questionId;
    private String questionText;
    private String selectedOption;
    private String correctOption;
    private boolean correct;

    public AnswerReviewDTO(Long questionId, String questionText, String selectedOption, String correctOption, boolean correct) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.correct = correct;
    }

    public Long getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public String getSelectedOption() { return selectedOption; }
    public String getCorrectOption() { return correctOption; }
    public boolean isCorrect() { return correct; }
}
