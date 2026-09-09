package com.deepaksir.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    
    @NotBlank(message = "Course title is required")
    private String title;
    
    private String description;
    private String thumbnailUrl;
    private BigDecimal price = BigDecimal.ZERO;
    private boolean isPremium = false;
    private Boolean published = false;
    private String courseType;
    private String duration;
    private boolean featured = false;
    private UUID examId;
}