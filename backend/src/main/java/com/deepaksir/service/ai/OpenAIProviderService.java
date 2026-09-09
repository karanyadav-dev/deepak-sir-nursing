package com.deepaksir.service.ai;

import com.deepaksir.config.AIConfig;
import com.deepaksir.dto.AIMessageDTO;
import com.deepaksir.entity.AIChat;
import com.deepaksir.exception.AIServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.util.*;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIProviderService implements AIProviderService {
    
    private final AIConfig aiConfig;
    private final RestTemplate restTemplate;
    
    @Override
    public String generateTextResponse(String prompt, String systemPrompt,
                                      List<AIMessageDTO> conversationHistory,
                                      String language) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getTextModel());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // Add system prompt
            messages.add(Map.of(
                "role", "system",
                "content", systemPrompt + "\n\nLanguage: " + getLanguageInstruction(language)
            ));
            
            // Add conversation history
            for (AIMessageDTO msg : conversationHistory) {
                messages.add(Map.of(
                    "role", msg.getRole().toString().toLowerCase(),
                    "content", msg.getContent() != null ? msg.getContent() : ""
                ));
            }
            
            // Add current prompt
            messages.add(Map.of(
                "role", "user",
                "content", prompt
            ));
            
            requestBody.put("messages", messages);
            
            return callOpenAI(requestBody);
        } catch (Exception e) {
            log.error("Error calling OpenAI text API", e);
            throw new AIServiceException("Failed to generate AI response", e);
        }
    }
    
    @Override
    public String generateVisionResponse(String prompt, MultipartFile image,
                                        String systemPrompt, String language) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String imageType = getImageMimeType(image.getContentType());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getVisionModel());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());
            
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // System prompt
            messages.add(Map.of(
                "role", "system",
                "content", systemPrompt + "\n\nYou are analyzing an educational medical image. " +
                           "Provide educational explanation. Do not give medical diagnosis."
            ));
            
            // User message with image
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            
            List<Map<String, Object>> content = new ArrayList<>();
            content.add(Map.of("type", "text", "text", prompt));
            content.add(Map.of(
                "type", "image_url",
                "image_url", Map.of(
                    "url", "data:" + imageType + ";base64," + base64Image
                )
            ));
            
            userMessage.put("content", content);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            return callOpenAI(requestBody);
        } catch (Exception e) {
            log.error("Error calling OpenAI vision API", e);
            throw new AIServiceException("Failed to analyze image", e);
        }
    }
    
    @Override
    public String generateMCQs(String topic, String difficulty, int count, String language) {
        String prompt = String.format(
            "Generate %d %s level MCQs about %s for nursing exam preparation. " +
            "Format each MCQ with:\n" +
            "Question\nA. Option\nB. Option\nC. Option\nD. Option\n\n" +
            "Correct Answer: [A/B/C/D]\nExplanation: [brief explanation]\n" +
            "Difficulty: %s\n\n" +
            "Make questions relevant to nursing competitive exams.",
            count, difficulty, topic, difficulty
        );
        
        return generateTextResponse(prompt, aiConfig.getSystemPrompt(), 
                                   Collections.emptyList(), language);
    }
    
    @Override
    public String generateCarePlan(String condition, String language) {
        String prompt = String.format(
            "Create a comprehensive educational nursing care plan for a patient with %s. " +
            "Include:\n" +
            "1. Assessment (subjective and objective data)\n" +
            "2. Nursing Diagnosis (using NANDA format)\n" +
            "3. Goals (short-term and long-term)\n" +
            "4. Nursing Interventions (with rationales)\n" +
            "5. Monitoring Parameters\n" +
            "6. Patient Education\n" +
            "7. Evaluation Criteria\n\n" +
            "This is for educational purposes only.",
            condition
        );
        
        return generateTextResponse(prompt, aiConfig.getSystemPrompt(), 
                                   Collections.emptyList(), language);
    }
    
    @Override
    public String generateNotes(String topic, String language) {
        String prompt = String.format(
            "Create comprehensive short notes for nursing students on: %s\n" +
            "Include:\n" +
            "- Definition\n" +
            "- Key Points\n" +
            "- Classification/Types\n" +
            "- Clinical Features\n" +
            "- Nursing Management\n" +
            "- Important Exam Points\n" +
            "- Quick Revision Points\n\n" +
            "Keep it concise and exam-oriented.",
            topic
        );
        
        return generateTextResponse(prompt, aiConfig.getSystemPrompt(), 
                                   Collections.emptyList(), language);
    }
    
    @Override
    public boolean supportsVision() {
        return aiConfig.getVisionModel() != null && !aiConfig.getVisionModel().isEmpty();
    }
    
    @Override
    public String getProviderName() {
        return "OpenAI";
    }
    
    private String callOpenAI(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiConfig.getApiKey());
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://api.openai.com/v1/chat/completions",
            HttpMethod.POST,
            request,
            Map.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        
        throw new AIServiceException("Invalid response from OpenAI API");
    }
    
    private String getLanguageInstruction(String language) {
        return switch (language.toLowerCase()) {
            case "hi" -> "Respond in Hindi language. Use Devanagari script.";
            case "hinglish" -> "Respond in Hinglish. Use Hindi terms in Roman script with English.";
            default -> "Respond in English. Include Hindi medical terms where helpful.";
        };
    }
    
    private String getImageMimeType(String contentType) {
        return switch (contentType != null ? contentType.toLowerCase() : "image/jpeg") {
            case "image/png" -> "image/png";
            case "image/webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }
}