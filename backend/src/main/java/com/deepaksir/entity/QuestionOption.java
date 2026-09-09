package com.deepaksir.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_options")
public class QuestionOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(nullable = false, length = 10)
    private String optionKey; // A, B, C, D
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionText;
    
    @Column(nullable = false)
    private boolean isCorrect = false;
    
    @Column(length = 500)
    private String imageUrl;
}