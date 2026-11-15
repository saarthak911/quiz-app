package com.quizapp.repository;

import com.quizapp.entity.QuizResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizResourceRepository extends JpaRepository<QuizResource, Long> {
    List<QuizResource> findByQuizIdOrderByIdAsc(Long quizId);
}
