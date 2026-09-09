package com.deepaksir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    
    private UUID id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private boolean isPremium;
    private boolean published;
    private UUID examId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCourseRequest {
        @NotBlank(message = "Course title is required")
        private String title;
        
        private String description;
        private BigDecimal price = BigDecimal.ZERO;
        private boolean isPremium = false;
        private boolean published = false;
        private UUID examId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCourseRequest {
        private String title;
        private String description;
        private BigDecimal price;
        private Boolean isPremium;
        private Boolean published;
        private UUID examId;
    }
}