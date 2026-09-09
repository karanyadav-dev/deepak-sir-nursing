package com.deepaksir.service;

import com.deepaksir.config.AIConfig;
import com.deepaksir.dto.AIChatDTO;
import com.deepaksir.dto.AIMessageDTO;
import com.deepaksir.entity.AIChat;
import com.deepaksir.entity.AIMessage;
import com.deepaksir.entity.User;
import com.deepaksir.exception.ApiException;
import com.deepaksir.exception.AIServiceException;
import com.deepaksir.repository.AIChatRepository;
import com.deepaksir.repository.AIMessageRepository;
import com.deepaksir.repository.UserRepository;
import com.deepaksir.service.ai.AIProviderService;
import com.deepaksir.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAssistantService {
    
    private final AIChatRepository chatRepository;
    private final AIMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AIProviderService aiProvider;
    private final AIConfig aiConfig;
    private final StorageService storageService;
    private final RedisTemplate<String, String> redisTemplate;
    private final StudentProgressService progressService;
    
    @Transactional
    public AIChatDTO createChat(UUID userId, AIChatDTO.CreateChatRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException("User not found"));
        
        long chatCount = chatRepository.countByUserIdAndActiveTrue(userId);
        if (chatCount >= aiConfig.getMaxChatsPerUser()) {
            throw new ApiException("Maximum chat limit reached");
        }
        
        AIChat chat = new AIChat();
        chat.setUser(user);
        chat.setTitle(request.getTitle());
        chat.setMode(request.getMode() != null ? request.getMode() : AIChat.ChatMode.GENERAL);
        
        AIChat saved = chatRepository.save(chat);
        return mapToDTO(saved);
    }
    
    @Transactional
    public AIMessageDTO sendMessage(UUID userId, UUID chatId, 
                                   AIMessageDTO.SendMessageRequest request) {
        checkRateLimit(userId);
        
        AIChat chat = chatRepository.findByIdAndUserIdAndActiveTrue(chatId, userId)
            .orElseThrow(() -> new ApiException("Chat not found"));
        
        // Save user message
        AIMessage userMessage = new AIMessage();
        userMessage.setChat(chat);
        userMessage.setRole(AIMessage.MessageRole.USER);
        userMessage.setContent(request.getContent());
        
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            validateImage(request.getImage());
            String imageUrl = storageService.storeImage(request.getImage(), "ai-chat-images");
            userMessage.setImageUrl(imageUrl);
            userMessage.setImageType(request.getImage().getContentType());
        }
        
        userMessage = messageRepository.save(userMessage);
        
        // Generate AI response
        String aiResponse = generateAIResponse(chat, userMessage, request);
        
        // Save AI response
        AIMessage assistantMessage = new AIMessage();
        assistantMessage.setChat(chat);
        assistantMessage.setRole(AIMessage.MessageRole.ASSISTANT);
        assistantMessage.setContent(aiResponse);
        assistantMessage = messageRepository.save(assistantMessage);
        
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        return mapToDTO(assistantMessage);
    }
    
    @Transactional(readOnly = true)
    public List<AIChatDTO> getUserChats(UUID userId, int limit) {
        return chatRepository.findByUserIdAndActiveTrueOrderByUpdatedAtDesc(userId)
            .stream()
            .limit(limit)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AIChatDTO getChat(UUID userId, UUID chatId) {
        AIChat chat = chatRepository.findByIdAndUserIdAndActiveTrue(chatId, userId)
            .orElseThrow(() -> new ApiException("Chat not found"));
        return mapToDTO(chat);
    }
    
    @Transactional
    public void deleteChat(UUID userId, UUID chatId) {
        AIChat chat = chatRepository.findByIdAndUserIdAndActiveTrue(chatId, userId)
            .orElseThrow(() -> new ApiException("Chat not found"));
        chat.setActive(false);
        chatRepository.save(chat);
    }
    
    @Transactional
    public AIChatDTO updateChat(UUID userId, UUID chatId, 
                               AIChatDTO.UpdateChatRequest request) {
        AIChat chat = chatRepository.findByIdAndUserIdAndActiveTrue(chatId, userId)
            .orElseThrow(() -> new ApiException("Chat not found"));
        
        if (request.getTitle() != null) {
            chat.setTitle(request.getTitle());
        }
        if (request.getMode() != null) {
            chat.setMode(request.getMode());
        }
        
        AIChat updated = chatRepository.save(chat);
        return mapToDTO(updated);
    }
    
    @Transactional(readOnly = true)
    public List<AIChatDTO> searchChats(UUID userId, String searchTerm) {
        return chatRepository.searchUserChats(userId, searchTerm)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public String generateQuickResponse(UUID userId, String mode, String topic, 
                                       String language) {
        checkRateLimit(userId);
        
        return switch (mode.toLowerCase()) {
            case "mcq" -> aiProvider.generateMCQs(topic, "medium", 20, language);
            case "notes" -> aiProvider.generateNotes(topic, language);
            case "care_plan" -> aiProvider.generateCarePlan(topic, language);
            case "revision" -> generateRevision(topic, language);
            case "viva" -> generateVivaQuestions(topic, language);
            default -> generateExplanation(topic, language);
        };
    }
    
    private String generateAIResponse(AIChat chat, AIMessage userMessage, 
                                     AIMessageDTO.SendMessageRequest request) {
        List<AIMessageDTO> history = messageRepository
            .findByChatIdOrderByCreatedAtAsc(chat.getId())
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        
        String systemPrompt = buildSystemPrompt(chat.getMode());
        String language = request.getLanguage() != null ? request.getLanguage() : "en";
        
        if (userMessage.getImageUrl() != null && aiProvider.supportsVision()) {
            return aiProvider.generateVisionResponse(
                request.getContent(),
                request.getImage(),
                systemPrompt,
                language
            );
        } else {
            return aiProvider.generateTextResponse(
                request.getContent(),
                systemPrompt,
                history,
                language
            );
        }
    }
    
    private String buildSystemPrompt(AIChat.ChatMode mode) {
        StringBuilder prompt = new StringBuilder(aiConfig.getSystemPrompt());
        prompt.append("\n\n").append(aiConfig.getSafetyPrompt());
        
        if (mode != null) {
            switch (mode) {
                case EXPLAIN -> prompt.append("\n\nMode: Explain Topic - Provide clear, structured explanations.");
                case MCQ -> prompt.append("\n\nMode: MCQ Generator - Generate high-quality MCQs.");
                case NOTES -> prompt.append("\n\nMode: Notes Generator - Create concise study notes.");
                case CARE_PLAN -> prompt.append("\n\nMode: Nursing Care Plan - Generate educational care plans.");
                case MEDICINE -> prompt.append("\n\nMode: Medicine Information - Provide drug information.");
                case IMAGE_EXPLAIN -> prompt.append("\n\nMode: Image Analysis - Analyze medical images educationally.");
                default -> prompt.append("\n\nMode: General - Answer nursing questions helpfully.");
            }
        }
        
        return prompt.toString();
    }
    
    private void checkRateLimit(UUID userId) {
        String minuteKey = "ai:rate:" + userId + ":minute";
        String hourKey = "ai:rate:" + userId + ":hour";
        
        Long minuteCount = redisTemplate.opsForValue().increment(minuteKey);
        if (minuteCount != null && minuteCount == 1) {
            redisTemplate.expire(minuteKey, 1, TimeUnit.MINUTES);
        }
        
        Long hourCount = redisTemplate.opsForValue().increment(hourKey);
        if (hourCount != null && hourCount == 1) {
            redisTemplate.expire(hourKey, 1, TimeUnit.HOURS);
        }
        
        if (minuteCount != null && minuteCount > aiConfig.getMaxRequestsPerMinute()) {
            throw new AIServiceException("Rate limit exceeded. Please try again later.");
        }
    }
    
    private void validateImage(MultipartFile image) {
        if (image.getSize() > aiConfig.getMaxImageSize()) {
            throw new ApiException("Image size exceeds maximum limit");
        }
        
        String filename = image.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if (!aiConfig.getAllowedImageTypes().contains(extension)) {
                throw new ApiException("Unsupported image format");
            }
        }
    }
    
    private String generateRevision(String topic, String language) {
        String prompt = String.format(
            "Provide quick revision points for: %s\n" +
            "Focus on key definitions, classifications, exam points, and mnemonics.",
            topic
        );
        return aiProvider.generateTextResponse(prompt, aiConfig.getSystemPrompt(),
                                              java.util.Collections.emptyList(), language);
    }
    
    private String generateVivaQuestions(String topic, String language) {
        String prompt = String.format(
            "Act as a nursing examiner. Ask 5 viva questions about: %s\n" +
            "Start with basic questions and increase difficulty.",
            topic
        );
        return aiProvider.generateTextResponse(prompt, aiConfig.getSystemPrompt(),
                                              java.util.Collections.emptyList(), language);
    }
    
    private String generateExplanation(String topic, String language) {
        String prompt = String.format(
            "Explain %s in simple terms for nursing students.\n" +
            "Include definition, key concepts, clinical relevance, and nursing implications.",
            topic
        );
        return aiProvider.generateTextResponse(prompt, aiConfig.getSystemPrompt(),
                                              java.util.Collections.emptyList(), language);
    }
    
    private AIChatDTO mapToDTO(AIChat chat) {
        return AIChatDTO.builder()
            .id(chat.getId())
            .title(chat.getTitle())
            .mode(chat.getMode())
            .createdAt(chat.getCreatedAt())
            .updatedAt(chat.getUpdatedAt())
            .messages(chat.getMessages() != null ? chat.getMessages().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()) : java.util.Collections.emptyList())
            .build();
    }
    
    private AIMessageDTO mapToDTO(AIMessage message) {
        return AIMessageDTO.builder()
            .id(message.getId())
            .chatId(message.getChat().getId())
            .role(message.getRole())
            .content(message.getContent())
            .imageUrl(message.getImageUrl())
            .imageType(message.getImageType())
            .createdAt(message.getCreatedAt())
            .build();
    }
}