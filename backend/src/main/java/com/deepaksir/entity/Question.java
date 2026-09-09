package com.deepaksir.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_questions_subject", columnList = "subject_id"),
    @Index(name = "idx_questions_topic", columnList = "topic_id"),
    @Index(name = "idx_questions_difficulty", columnList = "difficulty")
})
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType = QuestionType.SINGLE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column(nullable = false, length = 10)
    private String correctAnswer;
    
    @Column(length = 500)
    private String optionA;
    
    @Column(length = 500)
    private String optionB;
    
    @Column(length = 500)
    private String optionC;
    
    @Column(length = 500)
    private String optionD;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;
    
    @Column(length = 4)
    private String year;
    
    @ElementCollection
    @CollectionTable(name = "question_tags", 
        joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum QuestionType {
        SINGLE, MULTIPLE, TRUE_FALSE
    }
    
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}