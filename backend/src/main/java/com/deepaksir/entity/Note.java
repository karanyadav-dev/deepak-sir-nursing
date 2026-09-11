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
@Table(name = "notes", indexes = {
    @Index(name = "idx_notes_subject", columnList = "subject_id"),
    @Index(name = "idx_notes_topic", columnList = "topic_id"),
    @Index(name = "idx_notes_published", columnList = "published"),
    @Index(name = "idx_notes_premium", columnList = "is_premium")
})
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    private boolean published = false;

    @Column(nullable = false)
    private boolean active = true;

    // Premium feature - notes locked until payment
    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;

    // Preview content shown before purchase
    @Column(name = "preview_content", columnDefinition = "TEXT")
    private String previewContent;

    // Price for premium note (0 for free)
    @Column(name = "price")
    private Double price = 0.0;

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
}