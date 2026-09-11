package com.deepaksir.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {

    private String provider = "groq";
    private int maxTokens = 2048;
    private double temperature = 0.7;
    private int timeout = 60000;
    private int maxImageSize = 5 * 1024 * 1024;
    private List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "webp");

    private int maxRequestsPerMinute = 10;
    private int maxRequestsPerHour = 100;
    private int maxChatsPerUser = 50;

    // Nested configs for providers
    private GroqConfig groq = new GroqConfig();
    private OpenAIConfig openai = new OpenAIConfig();

    private String systemPrompt = """
        You are a knowledgeable Nursing Education Assistant for the "Deepak Sir" 
        nursing exam preparation platform. Your role is to help nursing students 
        learn and prepare for competitive exams like NORCET, AIIMS Nursing, etc.
        
        Guidelines:
        1. Provide educational information about nursing, medical topics, anatomy, 
           physiology, pharmacology, and related subjects.
        2. Use simple language with Hindi/Hinglish terms where helpful.
        3. Structure responses with clear headings, bullet points, and important markers.
        4. Include nursing considerations and exam-relevant points.
        5. NEVER provide definitive medical diagnoses or prescriptions.
        6. For emergencies, always advise immediate professional medical help.
        7. Be supportive, encouraging, and exam-focused.
        """;

    private String safetyPrompt = """
        IMPORTANT SAFETY GUIDELINES:
        - Do not claim to diagnose any medical condition.
        - Do not prescribe medications or change dosages.
        - Do not advise stopping prescribed treatments.
        - If symptoms suggest emergency, advise immediate medical attention.
        - Always add educational disclaimer when discussing treatments.
        - Focus on nursing education, not medical practice.
        """;

    @Data
    public static class GroqConfig {
        private String apiKey = "";
        private String model = "llama-3.3-70b-versatile";
        private String visionModel = "llama-3.2-90b-vision-preview";
        private String baseUrl = "https://api.groq.com/openai/v1";
    }

    @Data
    public static class OpenAIConfig {
        private String apiKey = "";
        private String model = "gpt-4-vision-preview";
        private String baseUrl = "https://api.openai.com/v1";
    }
}