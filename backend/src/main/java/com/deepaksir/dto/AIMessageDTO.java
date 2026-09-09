package com.deepaksir.dto;

import com.deepaksir.entity.AIMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIMessageDTO {
    
    private UUID id;
    private UUID chatId;
    private AIMessage.MessageRole role;
    private String content;
    private String imageUrl;
    private String imageType;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        @NotBlank(message = "Message content is required")
        private String content;
        
        private MultipartFile image;
        private String mode;
        private String language = "en"; // en, hi, hinglish
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIResponse {
        private String content;
        private String model;
        private int tokensUsed;
        private LocalDateTime timestamp;
    }
}