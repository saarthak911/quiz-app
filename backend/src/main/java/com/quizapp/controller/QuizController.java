package com.quizapp.controller;

import com.quizapp.dto.*;
import com.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/quizzes")
    public List<QuizSummaryDTO> listQuizzes() {
        return quizService.listQuizzes();
    }

    @PostMapping("/quizzes/{id}/start")
    public StartAttemptResponse startAttempt(@PathVariable Long id, Authentication auth) {
        return quizService.startAttempt(id, auth.getName());
    }

    @PostMapping("/attempts/{id}/submit")
    public ResultDTO submitAttempt(@PathVariable Long id, @RequestBody SubmitAttemptRequest request, Authentication auth) {
        return quizService.submitAttempt(id, request, auth.getName());
    }

    @GetMapping("/attempts/{id}/result")
    public ResultDTO getResult(@PathVariable Long id, Authentication auth) {
        return quizService.getResult(id, auth.getName());
    }
}
