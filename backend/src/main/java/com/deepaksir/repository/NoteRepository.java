package com.deepaksir.repository;

import com.deepaksir.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();

    List<Note> findBySubjectIdAndPublishedTrueAndActiveTrue(UUID subjectId);

    List<Note> findByTopicIdAndPublishedTrueAndActiveTrue(UUID topicId);

    List<Note> findByActiveTrueOrderByCreatedAtDesc();

    List<Note> findByPremiumTrueAndPublishedTrueAndActiveTrue();
}