package com.deepaksir.service.ai;

import com.deepaksir.config.AIConfig;
import com.deepaksir.dto.AIMessageDTO;
import com.deepaksir.exception.AIServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.provider", havingValue = "groq", matchIfMissing = true)
public class GroqProviderService implements AIProviderService {

    private final AIConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateTextResponse(String prompt, String systemPrompt,
                                       List<AIMessageDTO> conversationHistory,
                                       String language) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getGroq().getModel());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system",
                    "content", systemPrompt + "\n\nLanguage: " + getLanguageInstruction(language)));

            if (conversationHistory != null) {
                for (AIMessageDTO msg : conversationHistory) {
                    if (msg.getContent() != null && !msg.getContent().isEmpty()
                            && !msg.getRole().equals(com.deepaksir.entity.AIMessage.MessageRole.SYSTEM)) {
                        messages.add(Map.of(
                                "role", msg.getRole().toString().toLowerCase(),
                                "content", msg.getContent()
                        ));
                    }
                }
            }

            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);

            return callGroq(requestBody);
        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage(), e);
            throw new AIServiceException("Groq API error: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateVisionResponse(String prompt, MultipartFile image,
                                         String systemPrompt, String language) {
        // Groq vision preview - fallback to text
        return generateTextResponse(
                prompt + "\n\n[Note: Image analysis requires the image description. Please describe your image in detail for best results.]",
                systemPrompt, Collections.emptyList(), language);
    }

    @Override
    public String generateMCQs(String topic, String difficulty, int count, String language) {
        String prompt = String.format(
                "Generate %d %s level MCQs about %s for nursing exam preparation. " +
                "Format each MCQ with:\nQuestion\nA. Option\nB. Option\nC. Option\nD. Option\n\n" +
                "Correct Answer: [A/B/C/D]\nExplanation: [brief explanation]\nDifficulty: %s",
                count, difficulty, topic, difficulty);
        return generateTextResponse(prompt,
                "You are a nursing education expert creating exam MCQs. Structure every question clearly.",
                Collections.emptyList(), language);
    }

    @Override
    public String generateCarePlan(String condition, String language) {
        String prompt = String.format(
                "Create a comprehensive educational nursing care plan for: %s\n\n" +
                "Include: Assessment, Nursing Diagnosis (NANDA format), Goals, " +
                "Nursing Interventions with rationales, Monitoring, Patient Education, Evaluation.",
                condition);
        return generateTextResponse(prompt,
                "You are an expert nursing educator creating educational care plans.",
                Collections.emptyList(), language);
    }

    @Override
    public String generateNotes(String topic, String language) {
        String prompt = String.format(
                "Create comprehensive short notes for nursing students on: %s\n\n" +
                "Include: Definition, Key Points, Classification, Clinical Features, " +
                "Nursing Management, Important Exam Points, Quick Revision.",
                topic);
        return generateTextResponse(prompt,
                "You are an expert nursing educator creating exam-oriented notes.",
                Collections.emptyList(), language);
    }

    @Override
    public boolean supportsVision() {
        return false;
    }

    @Override
    public String getProviderName() {
        return "Groq";
    }

    private String callGroq(Map<String, Object> requestBody) {
        String apiKey = aiConfig.getGroq().getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new AIServiceException("Groq API key not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    aiConfig.getGroq().getBaseUrl() + "/chat/completions",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return choices.get(0).path("message").path("content").asText();
                }
            }
            throw new AIServiceException("Empty response from Groq");
        } catch (AIServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Groq call failed", e);
            throw new AIServiceException("Groq API call failed: " + e.getMessage(), e);
        }
    }

    private String getLanguageInstruction(String language) {
        if (language == null) return "Respond in English.";
        return switch (language.toLowerCase()) {
            case "hi" -> "Respond in Hindi language using Devanagari script.";
            case "hinglish" -> "Respond in Hinglish mixing Hindi and English.";
            default -> "Respond in English. Include Hindi medical terms where helpful.";
        };
    }
}