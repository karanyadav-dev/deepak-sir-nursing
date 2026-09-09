package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Video;
import com.deepaksir.service.VideoService;
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
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Video>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "lessonId", required = false) UUID lessonId,
            @RequestParam(value = "courseId", required = false) UUID courseId) {

        Video video = videoService.uploadVideo(file, title, description, lessonId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Video uploaded successfully", video));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Video>> getVideo(@PathVariable UUID id) {
        Video video = videoService.getVideo(id);
        return ResponseEntity.ok(ApiResponse.success("Video retrieved", video));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<Video>>> getVideosByLesson(@PathVariable UUID lessonId) {
        List<Video> videos = videoService.getVideosByLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Videos retrieved", videos));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Video>>> getVideosByCourse(@PathVariable UUID courseId) {
        List<Video> videos = videoService.getVideosByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Videos retrieved", videos));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Video>>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(ApiResponse.success("All videos retrieved", videos));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<Video>> updateVideoMetadata(
            @PathVariable UUID id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {

        Video video = videoService.updateVideoMetadata(id, title, description);
        return ResponseEntity.ok(ApiResponse.success("Video updated", video));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable UUID id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok(ApiResponse.success("Video deleted", null));
    }
}