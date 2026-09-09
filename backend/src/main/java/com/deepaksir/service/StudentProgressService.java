package com.deepaksir.service;

import com.deepaksir.entity.QuestionAttempt;
import com.deepaksir.repository.QuestionAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentProgressService {
    
    private final QuestionAttemptRepository questionAttemptRepository;
    
    @Transactional(readOnly = true)
    public String getWeakTopicsForPrompt(UUID userId) {
        try {
            List<QuestionAttempt> recentAttempts = questionAttemptRepository
                .findRecentAttemptsByUserId(userId, 100);
            
            if (recentAttempts.isEmpty()) {
                return null;
            }
            
            // Group by topic and calculate accuracy
            Map<String, List<QuestionAttempt>> topicAttempts = recentAttempts.stream()
                .filter(a -> a.getQuestion().getTopic() != null)
                .collect(Collectors.groupingBy(
                    a -> a.getQuestion().getTopic().getName()
                ));
            
            // Find weak topics (accuracy < 60%)
            List<String> weakTopics = topicAttempts.entrySet().stream()
                .filter(entry -> {
                    long correct = entry.getValue().stream()
                        .filter(QuestionAttempt::isCorrect)
                        .count();
                    double accuracy = (double) correct / entry.getValue().size() * 100;
                    return accuracy < 60;
                })
                .sorted((e1, e2) -> {
                    double acc1 = getAccuracy(e1.getValue());
                    double acc2 = getAccuracy(e2.getValue());
                    return Double.compare(acc1, acc2);
                })
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            if (weakTopics.isEmpty()) {
                return "No significant weak areas identified";
            }
            
            return String.join(", ", weakTopics);
        } catch (Exception e) {
            log.error("Failed to get weak topics for user: {}", userId, e);
            return null;
        }
    }
    
    private double getAccuracy(List<QuestionAttempt> attempts) {
        if (attempts.isEmpty()) return 0;
        long correct = attempts.stream()
            .filter(QuestionAttempt::isCorrect)
            .count();
        return (double) correct / attempts.size() * 100;
    }
}