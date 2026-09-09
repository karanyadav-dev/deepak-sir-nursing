package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Bookmark;
import com.deepaksir.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Bookmark>>> getUserBookmarks() {
        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks();
        return ResponseEntity.ok(ApiResponse.success("Bookmarks retrieved", bookmarks));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Bookmark>> addBookmark(
            @RequestParam String type,
            @RequestParam UUID itemId) {
        Bookmark bookmark = bookmarkService.addBookmark(type, itemId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bookmark added", bookmark));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(@PathVariable UUID id) {
        bookmarkService.removeBookmark(id);
        return ResponseEntity.ok(ApiResponse.success("Bookmark removed", null));
    }
}