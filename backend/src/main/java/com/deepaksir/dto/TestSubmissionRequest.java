package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSubmissionRequest {
    
    @NotNull(message = "Attempt ID is required")
    private UUID attemptId;
    
    @NotNull(message = "Answers are required")
    private Map<UUID, String> answers;
    
    private Integer timeTakenSeconds;
}