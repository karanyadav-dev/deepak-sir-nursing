package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer maxMarks;
    private Double negativeMarking;
    private UUID examId;
    private UUID subjectId;
    private boolean isPremium;
    private Boolean active;
}