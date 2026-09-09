package com.deepaksir.dto;

public class ProgressResponse {
    
    private long totalQuestionsAttempted;
    private long correctAnswers;
    private double accuracy;
    private long totalTests;
    private double averageScore;
    private double bestScore;
    
    public long getTotalQuestionsAttempted() { return totalQuestionsAttempted; }
    public void setTotalQuestionsAttempted(long totalQuestionsAttempted) { this.totalQuestionsAttempted = totalQuestionsAttempted; }
    
    public long getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(long correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    
    public long getTotalTests() { return totalTests; }
    public void setTotalTests(long totalTests) { this.totalTests = totalTests; }
    
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
}