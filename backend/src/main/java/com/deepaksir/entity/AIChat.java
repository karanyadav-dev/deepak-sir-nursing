package com.deepaksir.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_chats", indexes = {
    @Index(name = "idx_ai_chats_user", columnList = "user_id"),
    @Index(name = "idx_ai_chats_updated", columnList = "updated_at")
})
public class AIChat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMode mode = ChatMode.GENERAL;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<AIMessage> messages = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ChatMode {
        GENERAL, EXPLAIN, MCQ, NOTES, CARE_PLAN, MEDICINE, IMAGE_EXPLAIN, 
        REVISION, VIVA, EXAM_PREP
    }
}