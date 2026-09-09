package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Lesson;
import com.deepaksir.repository.ChapterRepository;
import com.deepaksir.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByChapter(@PathVariable UUID chapterId) {
        List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Lessons retrieved", lessons));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Lesson>> createLesson(@RequestBody Map<String, Object> request) {
        Lesson lesson = new Lesson();
        lesson.setTitle((String) request.get("title"));
        lesson.setDescription((String) request.getOrDefault("description", ""));
        lesson.setOrderIndex((Integer) request.getOrDefault("orderIndex", 0));
        lesson.setDurationMinutes((Integer) request.getOrDefault("durationMinutes", 0));
        lesson.setPreview((Boolean) request.getOrDefault("isPreview", false));
        lesson.setVideoUrl((String) request.getOrDefault("videoUrl", null));
        lesson.setPdfUrl((String) request.getOrDefault("pdfUrl", null));
        lesson.setNotesUrl((String) request.getOrDefault("notesUrl", null));

        UUID chapterId = UUID.fromString((String) request.get("chapterId"));
        lesson.setChapter(chapterRepository.findById(chapterId).orElseThrow());

        Lesson saved = lessonRepository.save(lesson);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson created", saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable UUID id) {
        lessonRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted", null));
    }
}