package com.deepaksir.repository;

import com.deepaksir.entity.ClassProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassProgressRepository extends JpaRepository<ClassProgress, UUID> {

    Optional<ClassProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);

    List<ClassProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);

    List<ClassProgress> findByUserIdOrderByLastWatchedAtDesc(UUID userId);

    long countByUserIdAndCourseIdAndCompletedTrue(UUID userId, UUID courseId);

    long countByCourseIdAndLessonId(UUID courseId, UUID lessonId);
}