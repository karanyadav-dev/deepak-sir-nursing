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
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Note>>> getPublishedNotes() {
        List<Note> notes = noteService.getPublishedNotes();
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<List<Note>>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(ApiResponse.success("All notes retrieved", notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> getNoteById(@PathVariable UUID id) {
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(ApiResponse.success("Note retrieved", note));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesBySubject(@PathVariable UUID subjectId) {
        List<Note> notes = noteService.getNotesBySubject(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesByTopic(@PathVariable UUID topicId) {
        List<Note> notes = noteService.getNotesByTopic(topicId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved", notes));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<Note>> createNote(
            @RequestBody Note note,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID topicId) {

        Note created = noteService.createNote(note, subjectId, topicId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
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
}