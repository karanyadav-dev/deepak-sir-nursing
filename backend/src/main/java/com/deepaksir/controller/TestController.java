package com.deepaksir.controller;

import com.deepaksir.dto.*;
import com.deepaksir.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Tag(name = "Test Management", description = "APIs for mock tests")
public class TestController {
    
    private final TestService testService;
    
    @GetMapping
    @Operation(summary = "Get all available tests")
    public ResponseEntity<ApiResponse<Page<TestResponse>>> getTests(
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) Boolean isPremium,
            Pageable pageable) {
        
        Page<TestResponse> tests = testService.getTests(examType, subjectId, isPremium, pageable);
        return ResponseEntity.ok(ApiResponse.success("Tests retrieved", tests));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get test details")
    public ResponseEntity<ApiResponse<TestResponse>> getTestById(@PathVariable UUID id) {
        TestResponse test = testService.getTestById(id);
        return ResponseEntity.ok(ApiResponse.success("Test retrieved", test));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Create new test")
    public ResponseEntity<ApiResponse<TestResponse>> createTest(
            @Valid @RequestBody TestRequest request) {
        TestResponse test = testService.createTest(request);
        return ResponseEntity.ok(ApiResponse.success("Test created", test));
    }
    
    @PostMapping("/{testId}/start")
    @Operation(summary = "Start a test attempt")
    public ResponseEntity<ApiResponse<TestAttemptResponse>> startTest(
            @PathVariable UUID testId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        TestAttemptResponse attempt = testService.startTest(testId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Test started", attempt));
    }
    
    @PostMapping("/attempts/{attemptId}/submit")
    @Operation(summary = "Submit test answers")
    public ResponseEntity<ApiResponse<TestResultResponse>> submitTest(
            @PathVariable UUID attemptId,
            @Valid @RequestBody TestSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        TestResultResponse result = testService.submitTest(attemptId, request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Test submitted successfully", result));
    }
    
    @GetMapping("/attempts/{attemptId}/result")
    @Operation(summary = "Get test result")
    public ResponseEntity<ApiResponse<TestResultResponse>> getTestResult(
            @PathVariable UUID attemptId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        TestResultResponse result = testService.getTestResult(attemptId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Test result retrieved", result));
    }
    
    @GetMapping("/attempts")
    @Operation(summary = "Get user's test attempts")
    public ResponseEntity<ApiResponse<Page<TestAttemptSummary>>> getUserAttempts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        
        Page<TestAttemptSummary> attempts = testService.getUserAttempts(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Attempts retrieved", attempts));
    }
}