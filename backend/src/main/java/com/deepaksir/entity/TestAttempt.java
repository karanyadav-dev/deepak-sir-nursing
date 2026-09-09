package com.deepaksir.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_attempts")
public class TestAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private Integer totalQuestions = 0;
    
    private Integer correctAnswers = 0;
    private Integer wrongAnswers = 0;
    private Integer unanswered = 0;
    
    private Double score = 0.0;
    private Double percentage = 0.0;
    private Double accuracy = 0.0;
    
    private Integer timeTakenSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum AttemptStatus {
        IN_PROGRESS, COMPLETED, ABANDONED
    }
}