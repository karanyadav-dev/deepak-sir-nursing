package com.deepaksir.repository;

import com.deepaksir.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByChapterIdOrderByOrderIndexAsc(UUID chapterId);
    long countByChapterId(UUID chapterId);
}
