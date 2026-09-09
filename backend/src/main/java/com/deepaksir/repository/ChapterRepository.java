package com.deepaksir.repository;

import com.deepaksir.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByCourseIdOrderByOrderIndexAsc(UUID courseId);
    long countByCourseId(UUID courseId);
}
