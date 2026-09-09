package com.deepaksir.repository;

import com.deepaksir.entity.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID> {
    
    List<QuestionAttempt> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT qa FROM QuestionAttempt qa WHERE qa.user.id = :userId " +
           "ORDER BY qa.createdAt DESC LIMIT :limit")
    List<QuestionAttempt> findRecentAttemptsByUserId(@Param("userId") UUID userId, 
                                                      @Param("limit") int limit);
    
    long countByUserIdAndIsCorrectTrue(UUID userId);
    
    long countByUserId(UUID userId);
    
    @Query("SELECT qa FROM QuestionAttempt qa JOIN qa.question q " +
           "WHERE qa.user.id = :userId AND q.subject.id = :subjectId")
    List<QuestionAttempt> findByUserIdAndSubjectId(@Param("userId") UUID userId,
                                                    @Param("subjectId") UUID subjectId);
}
