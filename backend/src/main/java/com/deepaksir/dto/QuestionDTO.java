package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    
    private UUID id;
    private String questionText;
    private String imageUrl;
    private String questionType;
    private String difficulty;
    private String explanation;
    private String correctAnswer;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private UUID subjectId;
    private String subjectName;
    private UUID topicId;
    private String topicName;
    private UUID examId;
    private String examName;
    private String year;
    private Set<String> tags;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}