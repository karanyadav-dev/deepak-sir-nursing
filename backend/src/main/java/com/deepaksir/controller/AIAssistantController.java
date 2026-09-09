package com.deepaksir.controller;

import com.deepaksir.dto.AIChatDTO;
import com.deepaksir.dto.AIMessageDTO;
import com.deepaksir.dto.ApiResponse;
import com.deepaksir.service.AIAssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ai-assistant")
@RequiredArgsConstructor
public class AIAssistantController {
    
    private final AIAssistantService aiService;
    
    @PostMapping("/chats")
    public ResponseEntity<ApiResponse<AIChatDTO>> createChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AIChatDTO.CreateChatRequest request) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        AIChatDTO chat = aiService.createChat(userId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Chat created successfully", chat));
    }
    
    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<AIChatDTO>>> getUserChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "20") int limit) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<AIChatDTO> chats = aiService.getUserChats(userId, limit);
        
        return ResponseEntity.ok(ApiResponse.success("Chats retrieved successfully", chats));
    }
    
    @GetMapping("/chats/{chatId}")
    public ResponseEntity<ApiResponse<AIChatDTO>> getChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        AIChatDTO chat = aiService.getChat(userId, chatId);
        
        return ResponseEntity.ok(ApiResponse.success("Chat retrieved successfully", chat));
    }
    
    @PostMapping(value = "/chats/{chatId}/messages", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AIMessageDTO>> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "language", defaultValue = "en") String language) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        
        AIMessageDTO.SendMessageRequest request = new AIMessageDTO.SendMessageRequest();
        request.setContent(content);
        request.setImage(image);
        request.setLanguage(language);
        
        AIMessageDTO response = aiService.sendMessage(userId, chatId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", response));
    }
    
    @PostMapping("/quick-response")
    public ResponseEntity<ApiResponse<String>> getQuickResponse(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String mode,
            @RequestParam String topic,
            @RequestParam(defaultValue = "en") String language) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        String response = aiService.generateQuickResponse(userId, mode, topic, language);
        
        return ResponseEntity.ok(ApiResponse.success("Response generated", response));
    }
    
    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<ApiResponse<Void>> deleteChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        aiService.deleteChat(userId, chatId);
        
        return ResponseEntity.ok(ApiResponse.success("Chat deleted successfully", null));
    }
    
    @PutMapping("/chats/{chatId}")
    public ResponseEntity<ApiResponse<AIChatDTO>> updateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody AIChatDTO.UpdateChatRequest request) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        AIChatDTO updated = aiService.updateChat(userId, chatId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Chat updated successfully", updated));
    }
    
    @GetMapping("/chats/search")
    public ResponseEntity<ApiResponse<List<AIChatDTO>>> searchChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String q) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<AIChatDTO> results = aiService.searchChats(userId, q);
        
        return ResponseEntity.ok(ApiResponse.success("Search results", results));
    }
}