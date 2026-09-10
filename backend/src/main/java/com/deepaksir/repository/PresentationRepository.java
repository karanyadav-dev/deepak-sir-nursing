package com.deepaksir.repository;

import com.deepaksir.entity.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, UUID> {
    List<Presentation> findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
    List<Presentation> findBySubjectIdAndPublishedTrueAndActiveTrue(UUID subjectId);
}