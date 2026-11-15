package com.quizapp.controller;

import com.quizapp.dto.AttemptSummaryDTO;
import com.quizapp.dto.CreateResourceRequest;
import com.quizapp.dto.ResourceDTO;
import com.quizapp.dto.ResourceListDTO;
import com.quizapp.dto.UpdateThresholdRequest;
import com.quizapp.dto.CreateQuestionRequest;
import com.quizapp.dto.CreateQuizRequest;
import com.quizapp.dto.QuizSummaryDTO;
import com.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Everything under /api/admin/** is already restricted to ROLE_ADMIN by SecurityConfig.
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/quizzes")
    public QuizSummaryDTO createQuiz(@Valid @RequestBody CreateQuizRequest request, Authentication auth) {
        return quizService.createQuiz(request, auth.getName());
    }

    @PostMapping("/quizzes/{id}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public void addQuestion(@PathVariable Long id, @Valid @RequestBody CreateQuestionRequest request) {
        quizService.addQuestion(id, request);
    }

    @GetMapping("/quizzes/{id}/results")
    public List<AttemptSummaryDTO> results(@PathVariable Long id) {
        return quizService.allResultsForQuiz(id);
    }

    // --- Suggested YouTube videos for students who score below the threshold ---

    @GetMapping("/quizzes/{id}/resources")
    public ResourceListDTO listResources(@PathVariable Long id) {
        return quizService.listResources(id);
    }

    @PostMapping("/quizzes/{id}/resources")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDTO addResource(@PathVariable Long id, @Valid @RequestBody CreateResourceRequest request) {
        return quizService.addResource(id, request);
    }

    @DeleteMapping("/quizzes/{id}/resources/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id, @PathVariable Long resourceId) {
        quizService.deleteResource(id, resourceId);
    }

    @PutMapping("/quizzes/{id}/suggestion-threshold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateThreshold(@PathVariable Long id, @Valid @RequestBody UpdateThresholdRequest request) {
        quizService.updateSuggestionThreshold(id, request.getPercent());
    }
}
