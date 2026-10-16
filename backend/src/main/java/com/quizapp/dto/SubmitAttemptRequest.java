package com.quizapp.dto;

import java.util.List;

public class SubmitAttemptRequest {
    private List<AnswerSubmission> answers;

    public List<AnswerSubmission> getAnswers() { return answers; }
    public void setAnswers(List<AnswerSubmission> answers) { this.answers = answers; }
}
