package com.deepaksir.dto;

import com.deepaksir.entity.AIChat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatDTO {
    
    private UUID id;
    private String title;
    private AIChat.ChatMode mode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AIMessageDTO> messages;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChatRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        private AIChat.ChatMode mode = AIChat.ChatMode.GENERAL;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateChatRequest {
        private String title;
        private AIChat.ChatMode mode;
    }
}