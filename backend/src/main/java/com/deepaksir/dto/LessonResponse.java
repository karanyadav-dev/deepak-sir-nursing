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
public class LessonResponse {
    private UUID id;
    private String title;
    private String description;
    private String videoUrl;
    private String pdfUrl;
    private String notesUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
    private boolean isPreview;
    private boolean completed;
}
