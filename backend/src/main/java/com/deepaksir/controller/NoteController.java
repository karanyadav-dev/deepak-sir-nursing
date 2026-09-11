package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Note;
import com.deepaksir.entity.User;
import com.deepaksir.dto.UserPrincipal;
import com.deepaksir.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping
    public ResponseEntity<ApiResponse<List<Note>>> getPublishedNotes() {
        UUID userId = getCurrentUserId();
        List<Note> notes = noteService.getPublishedNotes(userId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> getNoteById(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        Note note = noteService.getNoteById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Note retrieved", note));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesBySubject(@PathVariable UUID subjectId) {
        UUID userId = getCurrentUserId();
        List<Note> notes = noteService.getNotesBySubject(subjectId, userId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesByTopic(@PathVariable UUID topicId) {
        UUID userId = getCurrentUserId();
        List<Note> notes = noteService.getNotesByTopic(topicId, userId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    // ========== ADMIN-ONLY ==========

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Note>>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(ApiResponse.success("All notes retrieved", notes));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Note>> createNote(
            @RequestBody Note note,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID topicId) {

        Note created = noteService.createNote(note, subjectId, topicId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Note>> updateNote(
            @PathVariable UUID id,
            @RequestBody Note note) {

        Note updated = noteService.updateNote(id, note);
        return ResponseEntity.ok(ApiResponse.success("Note updated", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.success("Note deleted", null));
    }

    private UUID getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getId();
            }
            if (principal instanceof User) {
                return ((User) principal).getId();
            }
        } catch (Exception e) {
            // Not authenticated
        }
        return null;
    }
}