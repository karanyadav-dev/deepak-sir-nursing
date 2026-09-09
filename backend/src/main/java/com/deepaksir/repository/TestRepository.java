package com.deepaksir.repository;

import com.deepaksir.entity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
    
    Page<Test> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Test> findBySubjectId(UUID subjectId, Pageable pageable);
    
    Page<Test> findByIsPremium(Boolean isPremium, Pageable pageable);
    
    List<Test> findByActiveTrueOrderByCreatedAtDesc();
    
    List<Test> findBySubjectIdAndActiveTrue(UUID subjectId);
}