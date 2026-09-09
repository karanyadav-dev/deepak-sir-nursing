package com.deepaksir.repository;

import com.deepaksir.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    
    List<Bookmark> findByUserId(UUID userId);
    
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<Bookmark> findByUserIdAndType(UUID userId, String type);
    
    boolean existsByUserIdAndItemId(UUID userId, UUID itemId);
    
    void deleteByUserIdAndItemId(UUID userId, UUID itemId);
}