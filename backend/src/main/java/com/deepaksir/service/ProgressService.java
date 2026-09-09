package com.deepaksir.service;

import com.deepaksir.dto.ProgressResponse;
import com.deepaksir.repository.QuestionAttemptRepository;
import com.deepaksir.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProgressService {
    
    private final QuestionAttemptRepository questionAttemptRepository;
    private final TestAttemptRepository testAttemptRepository;
    
    public ProgressService(QuestionAttemptRepository questionAttemptRepository,
                          TestAttemptRepository testAttemptRepository) {
        this.questionAttemptRepository = questionAttemptRepository;
        this.testAttemptRepository = testAttemptRepository;
    }
    
    public ProgressResponse getUserProgress(UUID userId) {
        ProgressResponse response = new ProgressResponse();
        
        // Question stats
        long totalQuestions = questionAttemptRepository.countByUserId(userId);
        long correctAnswers = questionAttemptRepository.countByUserIdAndIsCorrectTrue(userId);
        
        response.setTotalQuestionsAttempted(totalQuestions);
        response.setCorrectAnswers(correctAnswers);
        response.setAccuracy(totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0);
        
        // Test stats (if TestAttemptRepository exists)
        // For now, set defaults
        response.setTotalTests(0);
        response.setAverageScore(0);
        response.setBestScore(0);
        
        return response;
    }
}