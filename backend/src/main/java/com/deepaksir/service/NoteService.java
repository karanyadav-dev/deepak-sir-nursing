package com.deepaksir.service;

import com.deepaksir.entity.Note;
import com.deepaksir.entity.Subject;
import com.deepaksir.entity.Topic;
import com.deepaksir.entity.Enrollment;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.NoteRepository;
import com.deepaksir.repository.SubjectRepository;
import com.deepaksir.repository.TopicRepository;
import com.deepaksir.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Get all published notes for students.
     * Premium notes are returned with masked content.
     */
    @Transactional(readOnly = true)
    public List<Note> getPublishedNotes(UUID userId) {
        List<Note> notes = noteRepository.findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

    /**
     * Get all notes (admin view) - no masking.
     */
    @Transactional(readOnly = true)
    public List<Note> getAllNotes() {
        return noteRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    /**
     * Get note by ID with premium check.
     */
    @Transactional(readOnly = true)
    public Note getNoteById(UUID id, UUID userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
        return applyPremiumLock(note, userId);
    }

    /**
     * Get notes by subject with premium check.
     */
    @Transactional(readOnly = true)
    public List<Note> getNotesBySubject(UUID subjectId, UUID userId) {
        List<Note> notes = noteRepository.findBySubjectIdAndPublishedTrueAndActiveTrue(subjectId);
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

    /**
     * Get notes by topic with premium check.
     */
    @Transactional(readOnly = true)
    public List<Note> getNotesByTopic(UUID topicId, UUID userId) {
        List<Note> notes = noteRepository.findByTopicIdAndPublishedTrueAndActiveTrue(topicId);
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

    /**
     * Create new note (admin only).
     */
    @Transactional
    public Note createNote(Note note, UUID subjectId, UUID topicId) {
        if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ApiException("Subject not found"));
            note.setSubject(subject);
        }
        if (topicId != null) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ApiException("Topic not found"));
            note.setTopic(topic);
        }
        return noteRepository.save(note);
    }

    /**
     * Update note (admin only).
     */
    @Transactional
    public Note updateNote(UUID id, Note updated) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));

        if (updated.getTitle() != null) note.setTitle(updated.getTitle());
        if (updated.getContent() != null) note.setContent(updated.getContent());
        if (updated.getPdfUrl() != null) note.setPdfUrl(updated.getPdfUrl());
        if (updated.getImageUrl() != null) note.setImageUrl(updated.getImageUrl());
        if (updated.getPreviewContent() != null) note.setPreviewContent(updated.getPreviewContent());
        
        note.setPublished(updated.isPublished());
        note.setPremium(updated.isPremium());
        if (updated.getPrice() != null) note.setPrice(updated.getPrice());

        return noteRepository.save(note);
    }

    /**
     * Soft delete note (admin only).
     */
    @Transactional
    public void deleteNote(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
        note.setActive(false);
        noteRepository.save(note);
    }

    /**
     * Apply premium lock logic.
     * - If note is free → return as-is
     * - If note is premium and user has access → return full content
     * - If note is premium and user has no access → mask content with preview
     */
    private Note applyPremiumLock(Note note, UUID userId) {
        if (!note.isPremium()) {
            // Free note - return as-is
            return note;
        }

        // Check if user has access (enrolled in the course OR is admin)
        boolean hasAccess = false;
        
        if (userId != null) {
            // Check enrollment in any course linked to this subject
            if (note.getSubject() != null) {
                List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
                hasAccess = enrollments.stream()
                        .anyMatch(e -> e.getCourse() != null 
                                && e.getCourse().getChapters() != null
                                && e.getCourse().getChapters().stream()
                                        .anyMatch(ch -> ch.getSubjects() != null 
                                                && ch.getSubjects().contains(note.getSubject())));
            }
        }

        if (!hasAccess) {
            // Mask premium content
            Note previewNote = new Note();
            previewNote.setId(note.getId());
            previewNote.setTitle(note.getTitle());
            previewNote.setPreviewContent(note.getPreviewContent());
            previewNote.setPremium(true);
            previewNote.setPrice(note.getPrice());
            previewNote.setSubject(note.getSubject());
            previewNote.setTopic(note.getTopic());
            previewNote.setPublished(note.isPublished());
            previewNote.setActive(note.isActive());
            previewNote.setPdfUrl(null); // Hide PDF
            previewNote.setImageUrl(null); // Hide image
            previewNote.setContent("🔒 PREMIUM CONTENT\n\nThis is a premium note. Please purchase to view full content.\n\nPreview: " 
                    + (note.getPreviewContent() != null ? note.getPreviewContent() : "Purchase to view"));
            previewNote.setCreatedAt(note.getCreatedAt());
            previewNote.setUpdatedAt(note.getUpdatedAt());
            return previewNote;
        }

        // User has access - return full note
        return note;
    }
}