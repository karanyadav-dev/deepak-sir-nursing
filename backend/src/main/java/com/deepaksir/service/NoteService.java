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

    @Transactional(readOnly = true)
    public List<Note> getPublishedNotes(UUID userId) {
        List<Note> notes = noteRepository.findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes() {
        return noteRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Note getNoteById(UUID id, UUID userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
        return applyPremiumLock(note, userId);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesBySubject(UUID subjectId, UUID userId) {
        List<Note> notes = noteRepository.findBySubjectIdAndPublishedTrueAndActiveTrue(subjectId);
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByTopic(UUID topicId, UUID userId) {
        List<Note> notes = noteRepository.findByTopicIdAndPublishedTrueAndActiveTrue(topicId);
        return notes.stream()
                .map(note -> applyPremiumLock(note, userId))
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void deleteNote(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
        note.setActive(false);
        noteRepository.save(note);
    }

    /**
     * Premium lock:
     * - Free note → full content
     * - Premium + enrolled user → full content
     * - Premium + not enrolled → masked preview
     */
    private Note applyPremiumLock(Note note, UUID userId) {
        if (!note.isPremium()) {
            return note;
        }

        // Simple access check: user must be enrolled in ANY course
        boolean hasAccess = false;
        
        if (userId != null) {
            List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
            hasAccess = !enrollments.isEmpty();
        }

        if (!hasAccess) {
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
            previewNote.setPdfUrl(null);
            previewNote.setImageUrl(null);
            previewNote.setContent("🔒 PREMIUM CONTENT\n\nYe premium note hai. Full content ke liye purchase karein.\n\n" +
                    "Preview: " + (note.getPreviewContent() != null ? note.getPreviewContent() : "Purchase to view"));
            previewNote.setCreatedAt(note.getCreatedAt());
            previewNote.setUpdatedAt(note.getUpdatedAt());
            return previewNote;
        }

        return note;
    }
}