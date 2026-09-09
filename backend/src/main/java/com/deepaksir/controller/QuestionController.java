package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.dto.AnalyticsDTOs;
import com.deepaksir.dto.QuestionRequest;
import com.deepaksir.dto.QuestionResponse;
import com.deepaksir.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    
    private final QuestionService questionService;
    
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestions() {
        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(ApiResponse.success("Questions retrieved successfully", questions));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@PathVariable UUID id) {
        QuestionResponse question = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success("Question retrieved successfully", question));
    }
    
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getRandomQuestions(
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(defaultValue = "10") int count) {
        List<QuestionResponse> questions = questionService.getRandomQuestions(subjectId, topicId, count);
        return ResponseEntity.ok(ApiResponse.success("Random questions retrieved", questions));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @RequestBody QuestionRequest request) {
        QuestionResponse created = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question created successfully", created));
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsDTOs.BulkImportResponse>> bulkImportQuestions(
            @RequestBody AnalyticsDTOs.BulkImportRequest request) {
        
        AnalyticsDTOs.BulkImportResponse result = new AnalyticsDTOs.BulkImportResponse();
        result.setTotalImported(request.getQuestions() != null ? request.getQuestions().size() : 0);
        result.setSuccessCount(0);
        result.setFailureCount(0);
        
        if (request.getQuestions() != null) {
            for (QuestionRequest questionRequest : request.getQuestions()) {
                try {
                    questionService.createQuestion(questionRequest);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.setFailureCount(result.getFailureCount() + 1);
                }
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success("Bulk import completed", result));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable UUID id,
            @RequestBody QuestionRequest request) {
        QuestionResponse updated = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Question updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question deleted successfully", null));
    }
}