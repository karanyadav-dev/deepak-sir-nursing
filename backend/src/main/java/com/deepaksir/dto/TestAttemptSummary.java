package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAttemptSummary {
    
    private UUID attemptId;
    private UUID testId;
    private String testName;
    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unanswered;
    private Double score;
    private Double percentage;
    private Double accuracy;
    private Integer timeTakenSeconds;
    private LocalDateTime submittedAt;
    private String status;
    private List<SubjectPerformance> subjectPerformance;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPerformance {
        private String subjectName;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private Double accuracy;
    }
}