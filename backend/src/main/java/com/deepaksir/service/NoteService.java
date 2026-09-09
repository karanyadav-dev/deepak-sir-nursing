package com.deepaksir.service;

import com.deepaksir.entity.Note;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    @Transactional
    public Note createNote(String title, String content, String fileUrl, UUID lessonId, UUID courseId) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setFileUrl(fileUrl);

        if (lessonId != null) {
            com.deepaksir.entity.Lesson lesson = new com.deepaksir.entity.Lesson();
            lesson.setId(lessonId);
            note.setLesson(lesson);
        }

        if (courseId != null) {
            com.deepaksir.entity.Course course = new com.deepaksir.entity.Course();
            course.setId(courseId);
            note.setCourse(course);
        }

        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByLesson(UUID lessonId) {
        return noteRepository.findByLessonIdOrderByCreatedAtDesc(lessonId);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByCourse(UUID courseId) {
        return noteRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    @Transactional(readOnly = true)
    public Note getNote(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));
    }

    @Transactional
    public Note updateNote(UUID id, String title, String content) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Note not found"));

        if (title != null) note.setTitle(title);
        if (content != null) note.setContent(content);

        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(UUID id) {
        noteRepository.deleteById(id);
    }
}