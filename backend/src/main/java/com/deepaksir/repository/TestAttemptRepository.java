package com.deepaksir.repository;

import com.deepaksir.entity.TestAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, UUID> {
    
    Page<TestAttempt> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    List<TestAttempt> findByTestId(UUID testId);
    
    List<TestAttempt> findByUserIdAndStatus(UUID userId, TestAttempt.AttemptStatus status);
}