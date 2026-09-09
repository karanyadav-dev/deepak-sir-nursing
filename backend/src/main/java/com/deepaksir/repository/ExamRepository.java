package com.deepaksir.repository;

import com.deepaksir.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    
    List<Exam> findByActiveTrueOrderByName();
    
    Optional<Exam> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
}