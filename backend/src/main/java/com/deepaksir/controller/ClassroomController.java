package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/classroom")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
public class ClassroomController {

    /**
     * Start a classroom session - ONLY ADMIN/INSTRUCTOR.
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Map<String, Object>>> startClass(
            @RequestBody Map<String, Object> request) {
        
        // TODO: Implement classroom session logic
        return ResponseEntity.ok(ApiResponse.success("Class started",
                Map.of("sessionId", java.util.UUID.randomUUID().toString(),
                       "status", "ACTIVE")));
    }

    /**
     * End classroom session.
     */
    @PostMapping("/end/{sessionId}")
    public ResponseEntity<ApiResponse<String>> endClass(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.success("Class ended", sessionId));
    }

    /**
     * Get classroom content (PPT, PDF, 3D, Video).
     */
    @GetMapping("/content/{sessionId}")
    public ResponseEntity<ApiResponse<Object>> getContent(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.success("Content retrieved", null));
    }
}