package com.deepaksir.service;

import com.deepaksir.dto.PerformanceAnalyticsDTO;
import com.deepaksir.dto.SubjectPerformanceDTO;
import com.deepaksir.entity.TestAttempt;
import com.deepaksir.repository.TestAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final TestAttemptRepository testAttemptRepository;
    private final QuestionAttemptRepository questionAttemptRepository;
    private final StudySessionRepository studySessionRepository;
    
    @Transactional(readOnly = true)
    public PerformanceAnalyticsDTO getUserAnalytics(UUID userId, String period) {
        LocalDateTime startDate = getStartDate(period);
        
        List<TestAttempt> attempts = testAttemptRepository
            .findByUserIdAndCreatedAtAfter(userId, startDate);
        
        PerformanceAnalyticsDTO analytics = new PerformanceAnalyticsDTO();
        
        // Basic stats
        analytics.setTotalTests(attempts.size());
        analytics.setAverageScore(calculateAverageScore(attempts));
        analytics.setBestScore(calculateBestScore(attempts));
        analytics.setAccuracy(calculateAccuracy(userId, startDate));
        
        // Subject-wise performance
        analytics.setSubjectPerformance(calculateSubjectPerformance(userId, startDate));
        
        // Weekly trend
        analytics.setWeeklyTrend(calculateWeeklyTrend(userId, startDate));
        
        // Strengths and weaknesses
        analytics.setStrengths(identifyStrengths(analytics.getSubjectPerformance()));
        analytics.setWeaknesses(identifyWeaknesses(analytics.getSubjectPerformance()));
        
        return analytics;
    }
    
    private LocalDateTime getStartDate(String period) {
        return switch (period != null ? period.toLowerCase() : "month") {
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "quarter" -> LocalDateTime.now().minusMonths(3);
            case "year" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.now().minusMonths(1);
        };
    }
    
    private double calculateAverageScore(List<TestAttempt> attempts) {
        if (attempts.isEmpty()) return 0;
        return attempts.stream()
            .mapToDouble(TestAttempt::getScorePercentage)
            .average()
            .orElse(0);
    }
    
    private double calculateBestScore(List<TestAttempt> attempts) {
        return attempts.stream()
            .mapToDouble(TestAttempt::getScorePercentage)
            .max()
            .orElse(0);
    }
    
    private double calculateAccuracy(UUID userId, LocalDateTime startDate) {
        List<QuestionAttempt> questionAttempts = questionAttemptRepository
            .findByUserIdAndCreatedAtAfter(userId, startDate);
        
        if (questionAttempts.isEmpty()) return 0;
        
        long correct = questionAttempts.stream()
            .filter(QuestionAttempt::isCorrect)
            .count();
        
        return (double) correct / questionAttempts.size() * 100;
    }
    
    private Map<String, Double> calculateSubjectPerformance(UUID userId, LocalDateTime startDate) {
        List<QuestionAttempt> attempts = questionAttemptRepository
            .findByUserIdAndCreatedAtAfter(userId, startDate);
        
        Map<String, List<QuestionAttempt>> subjectAttempts = attempts.stream()
            .collect(Collectors.groupingBy(a -> a.getQuestion().getSubject().getName()));
        
        Map<String, Double> performance = new HashMap<>();
        for (Map.Entry<String, List<QuestionAttempt>> entry : subjectAttempts.entrySet()) {
            long correct = entry.getValue().stream()
                .filter(QuestionAttempt::isCorrect)
                .count();
            performance.put(entry.getKey(), 
                (double) correct / entry.getValue().size() * 100);
        }
        
        return performance;
    }
    
    private List<PerformanceAnalyticsDTO.WeeklyData> calculateWeeklyTrend(
            UUID userId, LocalDateTime startDate) {
        List<TestAttempt> attempts = testAttemptRepository
            .findByUserIdAndCreatedAtAfter(userId, startDate);
        
        Map<LocalDate, List<TestAttempt>> dailyAttempts = attempts.stream()
            .collect(Collectors.groupingBy(a -> a.getCreatedAt().toLocalDate()));
        
        List<PerformanceAnalyticsDTO.WeeklyData> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<TestAttempt> dayAttempts = dailyAttempts.getOrDefault(date, Collections.emptyList());
            
            PerformanceAnalyticsDTO.WeeklyData data = new PerformanceAnalyticsDTO.WeeklyData();
            data.setDate(date.toString());
            data.setAverageScore(calculateAverageScore(dayAttempts));
            data.setTestsTaken(dayAttempts.size());
            
            trend.add(data);
        }
        
        return trend;
    }
    
    private List<String> identifyStrengths(Map<String, Double> subjectPerformance) {
        return subjectPerformance.entrySet().stream()
            .filter(e -> e.getValue() >= 75)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    private List<String> identifyWeaknesses(Map<String, Double> subjectPerformance) {
        return subjectPerformance.entrySet().stream()
            .filter(e -> e.getValue() < 50)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}