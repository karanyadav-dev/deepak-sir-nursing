package com.deepaksir.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsDTOs {
    
    public static class ProgressOverview {
        private long totalQuestions;
        private long attemptedQuestions;
        private long correctAnswers;
        private double accuracy;
        
        public long getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }
        
        public long getAttemptedQuestions() { return attemptedQuestions; }
        public void setAttemptedQuestions(long attemptedQuestions) { this.attemptedQuestions = attemptedQuestions; }
        
        public long getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(long correctAnswers) { this.correctAnswers = correctAnswers; }
        
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    }
    
    public static class SubjectProgress {
        private String subjectName;
        private long totalQuestions;
        private long correctAnswers;
        private double accuracy;
        
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        
        public long getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }
        
        public long getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(long correctAnswers) { this.correctAnswers = correctAnswers; }
        
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    }
    
    public static class AnalyticsResponse {
        private ProgressOverview overview;
        private List<SubjectProgress> subjectProgress;
        private Map<String, Double> weeklyPerformance;
        
        public ProgressOverview getOverview() { return overview; }
        public void setOverview(ProgressOverview overview) { this.overview = overview; }
        
        public List<SubjectProgress> getSubjectProgress() { return subjectProgress; }
        public void setSubjectProgress(List<SubjectProgress> subjectProgress) { this.subjectProgress = subjectProgress; }
        
        public Map<String, Double> getWeeklyPerformance() { return weeklyPerformance; }
        public void setWeeklyPerformance(Map<String, Double> weeklyPerformance) { this.weeklyPerformance = weeklyPerformance; }
    }
    
    public static class Recommendation {
        private String type;
        private String title;
        private String description;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class StudySessionRequest {
        private Integer durationMinutes;
        private String subject;
        private String activityType;
        
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getActivityType() { return activityType; }
        public void setActivityType(String activityType) { this.activityType = activityType; }
    }
    
    public static class BulkImportRequest {
        private List<QuestionRequest> questions;
        
        public List<QuestionRequest> getQuestions() { return questions; }
        public void setQuestions(List<QuestionRequest> questions) { this.questions = questions; }
    }
    
    public static class BulkImportResponse {
        private int totalImported;
        private int successCount;
        private int failureCount;
        
        public int getTotalImported() { return totalImported; }
        public void setTotalImported(int totalImported) { this.totalImported = totalImported; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    }
}