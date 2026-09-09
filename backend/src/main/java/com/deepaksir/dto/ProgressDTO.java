package com.deepaksir.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    
    private Integer totalQuestionsAttempted;
    private Integer totalTestsCompleted;
    private Double averageScore;
    private Double accuracy;
    private Integer studyStreak;
    private Integer totalStudyMinutes;
    private Map<String, Double> subjectProgress;
    private List<String> completedCourses;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudySessionDTO {
        private String subjectName;
        private String topicName;
        private Integer durationMinutes;
        private String activityType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StreakDTO {
        private Integer currentStreak;
        private Integer longestStreak;
        private String lastStudyDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceAnalyticsDTO {
        private Integer totalTests;
        private Double averageScore;
        private Double bestScore;
        private Double accuracy;
        private Map<String, Double> subjectPerformance;
        private List<WeeklyData> weeklyTrend;
        private List<String> strengths;
        private List<String> weaknesses;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyData {
        private String date;
        private Double averageScore;
        private Integer testsTaken;
    }
}