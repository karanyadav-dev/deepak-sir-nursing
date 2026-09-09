package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Note;
import com.deepaksir.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesByLesson(@PathVariable UUID lessonId) {
        List<Note> notes = noteService.getNotesByLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesByCourse(@PathVariable UUID courseId) {
        List<Note> notes = noteService.getNotesByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> getNote(@PathVariable UUID id) {
        Note note = noteService.getNote(id);
        return ResponseEntity.ok(ApiResponse.success("Note retrieved", note));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Note>> createNote(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String content = (String) request.getOrDefault("content", "");
        String fileUrl = (String) request.getOrDefault("fileUrl", null);
        UUID lessonId = request.get("lessonId") != null ? UUID.fromString((String) request.get("lessonId")) : null;
        UUID courseId = request.get("courseId") != null ? UUID.fromString((String) request.get("courseId")) : null;

        Note note = noteService.createNote(title, content, fileUrl, lessonId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created", note));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Note>> updateNote(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {

        Note note = noteService.updateNote(id, request.get("title"), request.get("content"));
        return ResponseEntity.ok(ApiResponse.success("Note updated", note));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.success("Note deleted", null));
    }
}