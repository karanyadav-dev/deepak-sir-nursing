package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO {
    
    private UUID id;
    private String title;
    private String description;
    private Integer orderIndex;
    private List<LessonDTO> lessons;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonDTO {
        private UUID id;
        private String title;
        private String description;
        private String videoUrl;
        private Integer orderIndex;
        private Integer durationMinutes;
        private boolean isPreview;
    }
}