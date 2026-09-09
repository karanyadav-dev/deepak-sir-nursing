package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponse {
    private UUID id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private boolean isPremium;
    private boolean published;
    private UUID examId;
    private String examName;
    private List<ChapterResponse> chapters;
    private boolean enrolled;
    private Double progressPercentage;
}
