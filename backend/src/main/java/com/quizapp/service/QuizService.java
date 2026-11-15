package com.quizapp.service;

import com.quizapp.dto.*;
import com.quizapp.entity.*;
import com.quizapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AttemptRepository attemptRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private QuizResourceRepository quizResourceRepository;

    // --- Student-facing ---

    public List<QuizSummaryDTO> listQuizzes() {
        return quizRepository.findAll().stream()
                .map(q -> new QuizSummaryDTO(q.getId(), q.getTitle(), q.getDescription(),
                        q.getDurationMinutes(), q.getQuestions().size()))
                .toList();
    }

    @Transactional
    public StartAttemptResponse startAttempt(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        List<QuestionForStudentDTO> questionDtos = quiz.getQuestions().stream()
                .map(q -> new QuestionForStudentDTO(q.getId(), q.getQuestionText(),
                        q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()))
                .toList();

        return new StartAttemptResponse(attempt.getId(), quiz.getDurationMinutes(), attempt.getStartedAt(), questionDtos);
    }

    @Transactional
    public ResultDTO submitAttempt(Long attemptId, SubmitAttemptRequest request, String username) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This attempt does not belong to you");
        }
        if (attempt.getSubmittedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt already submitted");
        }

        // Server-side timer enforcement: reject if wall-clock time since start exceeds
        // the quiz duration by more than a small grace window, regardless of what the
        // client's JS timer says.
        long elapsedSeconds = ChronoUnit.SECONDS.between(attempt.getStartedAt(), LocalDateTime.now());
        long allowedSeconds = attempt.getQuiz().getDurationMinutes() * 60L + 15; // 15s grace for network latency
        if (elapsedSeconds > allowedSeconds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time limit exceeded for this attempt");
        }

        int score = 0;
        List<AnswerReviewDTO> review = new ArrayList<>();

        List<AnswerSubmission> submissions = request.getAnswers() == null ? List.of() : request.getAnswers();

        for (AnswerSubmission submission : submissions) {
            Question question = questionRepository.findById(submission.getQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid question id"));

            boolean correct = question.getCorrectOption().equalsIgnoreCase(submission.getSelectedOption());
            if (correct) score++;

            Answer answer = new Answer(attempt, question, submission.getSelectedOption(), correct);
            answerRepository.save(answer);

            review.add(new AnswerReviewDTO(question.getId(), question.getQuestionText(),
                    submission.getSelectedOption(), question.getCorrectOption(), correct));
        }

        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        return buildResult(attempt.getId(), attempt.getQuiz(), score, review);
    }

    public ResultDTO getResult(Long attemptId, String username) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This attempt does not belong to you");
        }
        if (attempt.getSubmittedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt not yet submitted");
        }

        List<Answer> answers = answerRepository.findByAttemptId(attemptId);
        List<AnswerReviewDTO> review = answers.stream()
                .map(a -> new AnswerReviewDTO(a.getQuestion().getId(), a.getQuestion().getQuestionText(),
                        a.getSelectedOption(), a.getQuestion().getCorrectOption(), a.isCorrect()))
                .toList();

        return buildResult(attempt.getId(), attempt.getQuiz(), attempt.getScore(), review);
    }

    // Builds the result and decides, server-side, whether this student gets the
    // suggested videos: only when their percentage is below the quiz's threshold.
    private ResultDTO buildResult(Long attemptId, Quiz quiz, int score, List<AnswerReviewDTO> review) {
        int total = quiz.getQuestions().size();
        int threshold = quiz.getSuggestionThresholdPercent();

        List<ResourceDTO> suggestions = List.of();
        if (total > 0 && (score * 100.0 / total) < threshold) {
            suggestions = quizResourceRepository.findByQuizIdOrderByIdAsc(quiz.getId()).stream()
                    .map(r -> new ResourceDTO(r.getId(), r.getTitle(), r.getUrl()))
                    .toList();
        }
        return new ResultDTO(attemptId, score, total, review, threshold, suggestions);
    }

    // --- Admin-facing ---

    @Transactional
    public QuizSummaryDTO createQuiz(CreateQuizRequest request, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setCreatedBy(admin);
        quizRepository.save(quiz);

        return new QuizSummaryDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getDurationMinutes(), 0);
    }

    @Transactional
    public void addQuestion(Long quizId, CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption().toUpperCase());

        questionRepository.save(question);
    }

    public ResourceListDTO listResources(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        List<ResourceDTO> resources = quizResourceRepository.findByQuizIdOrderByIdAsc(quizId).stream()
                .map(r -> new ResourceDTO(r.getId(), r.getTitle(), r.getUrl()))
                .toList();
        return new ResourceListDTO(quiz.getSuggestionThresholdPercent(), resources);
    }

    @Transactional
    public ResourceDTO addResource(Long quizId, CreateResourceRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        QuizResource saved = quizResourceRepository.save(
                new QuizResource(quiz, request.getTitle().trim(), request.getUrl().trim()));
        return new ResourceDTO(saved.getId(), saved.getTitle(), saved.getUrl());
    }

    @Transactional
    public void deleteResource(Long quizId, Long resourceId) {
        QuizResource resource = quizResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        if (!resource.getQuiz().getId().equals(quizId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found for this quiz");
        }
        quizResourceRepository.delete(resource);
    }

    @Transactional
    public void updateSuggestionThreshold(Long quizId, int percent) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        quiz.setSuggestionThresholdPercent(percent);
        quizRepository.save(quiz);
    }

    public List<AttemptSummaryDTO> allResultsForQuiz(Long quizId) {
        return attemptRepository.findByQuizId(quizId).stream()
                .map(a -> new AttemptSummaryDTO(a.getId(), a.getUser().getUsername(), a.getScore(),
                        a.getStartedAt(), a.getSubmittedAt()))
                .toList();
    }
}
