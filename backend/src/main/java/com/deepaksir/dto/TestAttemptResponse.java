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
public class TestAttemptResponse {
    
    private UUID attemptId;
    private UUID testId;
    private String testName;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private List<QuestionDTO> questions;
    private String status;
}