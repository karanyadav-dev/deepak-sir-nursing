package com.deepaksir.repository;

import com.deepaksir.entity.AIChat;
import com.deepaksir.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AIChatRepository extends JpaRepository<AIChat, UUID> {
    
    List<AIChat> findByUserIdAndActiveTrueOrderByUpdatedAtDesc(UUID userId);
    
    List<AIChat> findByUserIdAndActiveTrueOrderByUpdatedAtDesc(
            UUID userId, Pageable pageable);
    
    Optional<AIChat> findByIdAndUserIdAndActiveTrue(UUID id, UUID userId);
    
    long countByUserIdAndActiveTrue(UUID userId);
    
    @Query("SELECT c FROM AIChat c WHERE c.user.id = :userId " +
           "AND c.active = true AND LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.updatedAt DESC")
    List<AIChat> searchUserChats(@Param("userId") UUID userId, 
                                  @Param("search") String search);
    
    void deleteByIdAndUserId(UUID id, UUID userId);
}