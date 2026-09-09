package com.deepaksir.controller;

import com.deepaksir.dto.AnalyticsDTOs;
import com.deepaksir.dto.ApiResponse;
import com.deepaksir.dto.ProgressResponse;
import com.deepaksir.dto.UserPrincipal;
import com.deepaksir.entity.User;
import com.deepaksir.service.ProgressService;
import com.deepaksir.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/progress")
public class ProgressController {
    
    private final ProgressService progressService;
    private final AnalyticsService analyticsService;
    
    public ProgressController(ProgressService progressService, 
                             AnalyticsService analyticsService) {
        this.progressService = progressService;
        this.analyticsService = analyticsService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<ProgressResponse>> getUserProgress(
            @AuthenticationPrincipal User user) {
        ProgressResponse progress = progressService.getUserProgress(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved successfully", progress));
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<AnalyticsDTOs.AnalyticsResponse>> getAnalytics(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "month") String period) {
        AnalyticsDTOs.AnalyticsResponse analytics = analyticsService.getUserAnalytics(user.getId(), period);
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<AnalyticsDTOs.Recommendation>>> getRecommendations(
            @AuthenticationPrincipal User user) {
        // TODO: Implement recommendations
        List<AnalyticsDTOs.Recommendation> recommendations = List.of();
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved", recommendations));
    }
    
    @PostMapping("/study-session")
    public ResponseEntity<ApiResponse<Void>> recordStudySession(
            @AuthenticationPrincipal User user,
            @RequestBody AnalyticsDTOs.StudySessionRequest request) {
        // TODO: Implement study session recording
        return ResponseEntity.ok(ApiResponse.success("Study session recorded", null));
    }
}