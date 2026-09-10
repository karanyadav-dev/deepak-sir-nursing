package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Presentation;
import com.deepaksir.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/presentations")
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationService presentationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Presentation>>> getPublishedPresentations() {
        List<Presentation> presentations = presentationService.getPublishedPresentations();
        return ResponseEntity.ok(ApiResponse.success("Presentations retrieved", presentations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Presentation>> getPresentationById(@PathVariable UUID id) {
        Presentation presentation = presentationService.getPresentationById(id);
        return ResponseEntity.ok(ApiResponse.success("Presentation retrieved", presentation));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<Presentation>> uploadPresentation(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "subjectId", required = false) UUID subjectId) {

        Presentation presentation = presentationService.uploadPresentation(file, title, description, subjectId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Presentation uploaded", presentation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePresentation(@PathVariable UUID id) {
        presentationService.deletePresentation(id);
        return ResponseEntity.ok(ApiResponse.success("Presentation deleted", null));
    }
}