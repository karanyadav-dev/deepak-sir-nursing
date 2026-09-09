package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Chapter;
import com.deepaksir.repository.ChapterRepository;
import com.deepaksir.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Chapter>>> getChaptersByCourse(@PathVariable UUID courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return ResponseEntity.ok(ApiResponse.success("Chapters retrieved", chapters));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Chapter>> createChapter(@RequestBody Map<String, Object> request) {
        Chapter chapter = new Chapter();
        chapter.setTitle((String) request.get("title"));
        chapter.setDescription((String) request.getOrDefault("description", ""));
        chapter.setOrderIndex((Integer) request.getOrDefault("orderIndex", 0));

        UUID courseId = UUID.fromString((String) request.get("courseId"));
        chapter.setCourse(courseRepository.findById(courseId).orElseThrow());

        Chapter saved = chapterRepository.save(chapter);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chapter created", saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(@PathVariable UUID id) {
        chapterRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Chapter deleted", null));
    }
}