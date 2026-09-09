package com.deepaksir.repository;

import com.deepaksir.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findByLessonIdOrderByCreatedAtDesc(UUID lessonId);

    List<Note> findByCourseIdOrderByCreatedAtDesc(UUID courseId);

    List<Note> findByTitleContainingIgnoreCase(String search);
}