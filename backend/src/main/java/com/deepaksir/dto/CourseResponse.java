package com.deepaksir.dto;

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
public class CourseResponse {

    private UUID id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private boolean isPremium;
    private boolean published;
    private String courseType;
    private String duration;
    private boolean featured;
    private UUID examId;
    private String examName;
    private Integer chapterCount;
    private Integer lessonCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}