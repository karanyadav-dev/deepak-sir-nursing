package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private LocalDateTime expiresAt;
    private String status;
    private Double progressPercentage;
}
