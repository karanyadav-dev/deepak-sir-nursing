package com.deepaksir.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    
    private String provider = "openai";
    private String apiKey;
    private String model = "gpt-4";
    private String textModel = "gpt-4";
    private String visionModel = "gpt-4-vision-preview";
    private int maxTokens = 2000;
    private double temperature = 0.7;
    private int timeout = 60000;
    private int maxImageSize = 5 * 1024 * 1024;
    private List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "webp");
    private int maxRequestsPerMinute = 10;
    private int maxRequestsPerHour = 100;
    private int maxChatsPerUser = 50;
    private String systemPrompt = "You are a Nursing Education Assistant.";
    private String safetyPrompt = "Provide educational information only.";
    
    // Getters and Setters
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getTextModel() { return textModel; }
    public void setTextModel(String textModel) { this.textModel = textModel; }
    
    public String getVisionModel() { return visionModel; }
    public void setVisionModel(String visionModel) { this.visionModel = visionModel; }
    
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public int getMaxImageSize() { return maxImageSize; }
    public void setMaxImageSize(int maxImageSize) { this.maxImageSize = maxImageSize; }
    
    public List<String> getAllowedImageTypes() { return allowedImageTypes; }
    public void setAllowedImageTypes(List<String> allowedImageTypes) { this.allowedImageTypes = allowedImageTypes; }
    
    public int getMaxRequestsPerMinute() { return maxRequestsPerMinute; }
    public void setMaxRequestsPerMinute(int maxRequestsPerMinute) { this.maxRequestsPerMinute = maxRequestsPerMinute; }
    
    public int getMaxRequestsPerHour() { return maxRequestsPerHour; }
    public void setMaxRequestsPerHour(int maxRequestsPerHour) { this.maxRequestsPerHour = maxRequestsPerHour; }
    
    public int getMaxChatsPerUser() { return maxChatsPerUser; }
    public void setMaxChatsPerUser(int maxChatsPerUser) { this.maxChatsPerUser = maxChatsPerUser; }
    
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    
    public String getSafetyPrompt() { return safetyPrompt; }
    public void setSafetyPrompt(String safetyPrompt) { this.safetyPrompt = safetyPrompt; }
}