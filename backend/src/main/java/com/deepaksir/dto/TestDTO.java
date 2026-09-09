package com.deepaksir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    
    private UUID id;
    private String name;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Double maxMarks;
    private Double negativeMarking;
    private UUID examId;
    private boolean isPremium;
    private boolean published;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTestRequest {
        @NotBlank(message = "Test name is required")
        private String name;
        
        private String description;
        
        @NotNull(message = "Duration is required")
        private Integer durationMinutes;
        
        @NotNull(message = "Total questions is required")
        private Integer totalQuestions;
        
        private Double maxMarks;
        private Double negativeMarking = 0.0;
        private UUID examId;
        private boolean isPremium = false;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestSubmissionDTO {
        @NotNull(message = "Answers are required")
        private Map<Integer, String> answers;
        
        private Integer timeTakenSeconds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestResultDTO {
        private UUID attemptId;
        private Integer totalQuestions;
        private Integer attempted;
        private Integer correct;
        private Integer incorrect;
        private Integer unanswered;
        private Double marksObtained;
        private Double maxMarks;
        private Double accuracy;
        private Integer timeTakenSeconds;
        private Map<String, Double> subjectWiseAccuracy;
        private List<String> strengths;
        private List<String> weaknesses;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestAttemptDTO {
        private UUID attemptId;
        private UUID testId;
        private String testName;
        private Integer durationSeconds;
        private Integer totalQuestions;
        private List<QuestionDTO> questions;
        private LocalDateTime startedAt;
        private LocalDateTime expiresAt;
    }
}