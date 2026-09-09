package com.deepaksir.repository;

import com.deepaksir.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    List<Video> findByLessonIdOrderByCreatedAtDesc(UUID lessonId);

    List<Video> findByCourseIdOrderByCreatedAtDesc(UUID courseId);

    List<Video> findByActiveTrueOrderByCreatedAtDesc();

    long countByLessonId(UUID lessonId);
}