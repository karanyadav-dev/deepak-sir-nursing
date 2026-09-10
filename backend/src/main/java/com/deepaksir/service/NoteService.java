package com.deepaksir.service;

import com.deepaksir.entity.Note;
import com.deepaksir.entity.Subject;
import com.deepaksir.entity.Topic;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.NoteRepository;
import com.deepaksir.repository.SubjectRepository;
import com.deepaksir.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    @Transactional(readOnly = true)
    public List<Note> getPublishedNotes() {
        return noteRepository.findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes() {
        return noteRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Note getNoteById(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesBySubject(UUID subjectId) {
        return noteRepository.findBySubjectIdAndPublishedTrueAndActiveTrue(subjectId);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByTopic(UUID topicId) {
        return noteRepository.findByTopicIdAndPublishedTrueAndActiveTrue(topicId);
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
        note.setPublished(updated.isPublished());

        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
        note.setActive(false);
        noteRepository.save(note);
    }
}