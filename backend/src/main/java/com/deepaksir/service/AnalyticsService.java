package com.deepaksir.service;

import com.deepaksir.dto.AnalyticsDTOs;
import com.deepaksir.repository.QuestionAttemptRepository;
import com.deepaksir.repository.StudySessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnalyticsService {
    
    private final QuestionAttemptRepository questionAttemptRepository;
    private final StudySessionRepository studySessionRepository;
    
    public AnalyticsService(QuestionAttemptRepository questionAttemptRepository,
                           StudySessionRepository studySessionRepository) {
        this.questionAttemptRepository = questionAttemptRepository;
        this.studySessionRepository = studySessionRepository;
    }
    
    public AnalyticsDTOs.AnalyticsResponse getUserAnalytics(UUID userId, String period) {
        AnalyticsDTOs.AnalyticsResponse response = new AnalyticsDTOs.AnalyticsResponse();
        
        AnalyticsDTOs.ProgressOverview overview = new AnalyticsDTOs.ProgressOverview();
        long totalAttempts = questionAttemptRepository.countByUserId(userId);
        long correctAttempts = questionAttemptRepository.countByUserIdAndIsCorrectTrue(userId);
        
        overview.setTotalQuestions(totalAttempts);
        overview.setAttemptedQuestions(totalAttempts);
        overview.setCorrectAnswers(correctAttempts);
        overview.setAccuracy(totalAttempts > 0 ? (double) correctAttempts / totalAttempts * 100 : 0);
        
        response.setOverview(overview);
        
        return response;
    }
}