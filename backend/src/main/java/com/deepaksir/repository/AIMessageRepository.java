package com.deepaksir.repository;

import com.deepaksir.entity.AIMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AIMessageRepository extends JpaRepository<AIMessage, UUID> {
    
    List<AIMessage> findByChatIdOrderByCreatedAtAsc(UUID chatId);
    
    List<AIMessage> findByChatIdAndActiveTrueOrderByCreatedAtAsc(UUID chatId);
    
    long countByChatId(UUID chatId);
    
    void deleteByChatId(UUID chatId);
}