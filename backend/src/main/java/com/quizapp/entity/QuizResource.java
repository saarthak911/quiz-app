package com.quizapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// A YouTube link an admin attaches to a quiz. Students who score below the
// quiz's suggestion threshold see these links on their result page.
@Entity
@Table(name = "quiz_resources")
public class QuizResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String url;

    private LocalDateTime createdAt = LocalDateTime.now();

    public QuizResource() {}

    public QuizResource(Quiz quiz, String title, String url) {
        this.quiz = quiz;
        this.title = title;
        this.url = url;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
