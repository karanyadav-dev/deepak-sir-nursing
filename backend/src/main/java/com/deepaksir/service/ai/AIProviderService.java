package com.deepaksir.service.ai;

import com.deepaksir.dto.AIMessageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AIProviderService {
    
    String generateTextResponse(String prompt, String systemPrompt,
                               List<AIMessageDTO> conversationHistory,
                               String language);
    
    String generateVisionResponse(String prompt, MultipartFile image,
                                 String systemPrompt, String language);
    
    String generateMCQs(String topic, String difficulty, int count, String language);
    
    String generateCarePlan(String condition, String language);
    
    String generateNotes(String topic, String language);
    
    boolean supportsVision();
    
    String getProviderName();
}