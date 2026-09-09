package com.deepaksir.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressRequest {
    @NotNull(message = "Lesson ID is required")
    private UUID lessonId;
    
    @NotNull(message = "Completed status is required")
    private Boolean completed;
    
    private Integer watchDurationSeconds;
}
