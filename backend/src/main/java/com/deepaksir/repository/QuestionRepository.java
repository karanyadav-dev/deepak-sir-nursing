package com.deepaksir.repository;

import com.deepaksir.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    
    List<Question> findBySubjectId(UUID subjectId);
    
    List<Question> findByTopicId(UUID topicId);
    
    List<Question> findBySubjectIdAndTopicId(UUID subjectId, UUID topicId);
    
    List<Question> findByActiveTrue();
    
    List<Question> findBySubjectIdOrderByCreatedAtAsc(UUID subjectId);
    
    // Random questions using PostgreSQL RANDOM() function
    @Query(value = "SELECT * FROM questions WHERE active = true ORDER BY RANDOM() LIMIT :limit", 
           nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
    
    // Random questions by subject using PostgreSQL RANDOM() function
    @Query(value = "SELECT * FROM questions WHERE subject_id = :subjectId AND active = true ORDER BY RANDOM() LIMIT :limit", 
           nativeQuery = true)
    List<Question> findRandomQuestionsBySubject(@Param("subjectId") UUID subjectId, 
                                                 @Param("limit") int limit);
}