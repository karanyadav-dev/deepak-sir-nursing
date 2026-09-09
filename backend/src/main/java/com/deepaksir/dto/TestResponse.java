package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {
    
    private UUID id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer maxMarks;
    private Double negativeMarking;
    private Boolean isPremium;
    private Boolean active;
    private UUID subjectId;
    private String subjectName;
    private UUID examId;
    private String examName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}