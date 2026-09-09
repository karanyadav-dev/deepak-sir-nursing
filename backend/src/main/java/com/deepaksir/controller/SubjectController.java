package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.dto.SubjectDTO;
import com.deepaksir.dto.TopicDTO;
import com.deepaksir.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    
    private final SubjectService subjectService;
    
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDTO>>> getAllSubjects() {
        List<SubjectDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", subjects));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDTO>> getSubjectById(@PathVariable UUID id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Subject retrieved successfully", subject));
    }
    
    @GetMapping("/{id}/topics")
    public ResponseEntity<ApiResponse<List<TopicDTO>>> getSubjectTopics(@PathVariable UUID id) {
        List<TopicDTO> topics = subjectService.getSubjectTopics(id);
        return ResponseEntity.ok(ApiResponse.success("Topics retrieved successfully", topics));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDTO>> createSubject(@RequestBody SubjectDTO subjectDTO) {
        SubjectDTO created = subjectService.createSubject(subjectDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", created));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDTO>> updateSubject(
            @PathVariable UUID id,
            @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO updated = subjectService.updateSubject(id, subjectDTO);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }
}