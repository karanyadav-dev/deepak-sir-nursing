package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.ClassProgress;
import com.deepaksir.service.ClassProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ClassProgressController {

    private final ClassProgressService progressService;

    @PostMapping("/classes/{lessonId}")
    public ResponseEntity<ApiResponse<ClassProgress>> saveProgress(
            @PathVariable UUID lessonId,
            @RequestBody Map<String, Object> request) {

        int watchedSeconds = (int) request.getOrDefault("watchedSeconds", 0);
        int totalSeconds = (int) request.getOrDefault("totalSeconds", 0);
        boolean completed = (boolean) request.getOrDefault("completed", false);

        ClassProgress progress = progressService.saveProgress(
                lessonId, watchedSeconds, totalSeconds, completed);
        return ResponseEntity.ok(ApiResponse.success("Progress saved", progress));
    }

    @GetMapping("/classes/{lessonId}")
    public ResponseEntity<ApiResponse<ClassProgress>> getProgress(@PathVariable UUID lessonId) {
        ClassProgress progress = progressService.getProgress(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved", progress));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseProgress(
            @PathVariable UUID courseId) {

        List<ClassProgress> progressList = progressService.getCourseProgress(courseId);
        double percentage = progressService.calculateCourseProgress(courseId);

        Map<String, Object> response = Map.of(
                "progressPercentage", percentage,
                "completedClasses", progressList.stream().filter(ClassProgress::isCompleted).count(),
                "totalClasses", progressList.size(),
                "progressList", progressList
        );

        return ResponseEntity.ok(ApiResponse.success("Course progress retrieved", response));
    }

    @GetMapping("/continue-watching")
    public ResponseEntity<ApiResponse<List<ClassProgress>>> getContinueWatching() {
        List<ClassProgress> progress = progressService.getContinueWatching();
        return ResponseEntity.ok(ApiResponse.success("Continue watching retrieved", progress));
    }
}