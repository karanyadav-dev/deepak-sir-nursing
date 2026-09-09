package com.deepaksir.service;

import com.deepaksir.entity.Video;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.VideoRepository;
import com.deepaksir.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final StorageService storageService;

    @Value("${storage.local.path:./uploads}")
    private String basePath;

    private static final long MAX_VIDEO_SIZE = 500 * 1024 * 1024; // 500 MB
    private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
            "video/mp4", "video/webm", "video/mkv", "video/avi", "video/quicktime"
    );

    @Transactional
    public Video uploadVideo(MultipartFile file, String title, String description,
                            UUID lessonId, UUID courseId) {

        // Validate file
        validateVideo(file);

        try {
            // Create directory
            Path videoDir = Paths.get(basePath, "videos");
            Files.createDirectories(videoDir);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = videoDir.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Create video record
            Video video = new Video();
            video.setTitle(title != null ? title : originalFilename);
            video.setDescription(description);
            video.setFilePath(filePath.toString());
            video.setFileUrl("/api/files/videos/" + filename);
            video.setFileSize(file.getSize());
            video.setMimeType(file.getContentType());

            if (lessonId != null) {
                com.deepaksir.entity.Lesson lesson = new com.deepaksir.entity.Lesson();
                lesson.setId(lessonId);
                video.setLesson(lesson);
            }

            if (courseId != null) {
                com.deepaksir.entity.Course course = new com.deepaksir.entity.Course();
                course.setId(courseId);
                video.setCourse(course);
            }

            return videoRepository.save(video);

        } catch (IOException e) {
            log.error("Failed to upload video", e);
            throw new ApiException("Failed to upload video: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Video getVideo(UUID id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ApiException("Video not found"));
    }

    @Transactional(readOnly = true)
    public List<Video> getVideosByLesson(UUID lessonId) {
        return videoRepository.findByLessonIdOrderByCreatedAtDesc(lessonId);
    }

    @Transactional(readOnly = true)
    public List<Video> getVideosByCourse(UUID courseId) {
        return videoRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    @Transactional(readOnly = true)
    public List<Video> getAllVideos() {
        return videoRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public void deleteVideo(UUID id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ApiException("Video not found"));

        // Delete file from storage
        try {
            Files.deleteIfExists(Paths.get(video.getFilePath()));
        } catch (IOException e) {
            log.warn("Failed to delete video file: {}", video.getFilePath(), e);
        }

        video.setActive(false);
        videoRepository.save(video);
    }

    @Transactional
    public Video updateVideoMetadata(UUID id, String title, String description) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ApiException("Video not found"));

        if (title != null) video.setTitle(title);
        if (description != null) video.setDescription(description);

        return videoRepository.save(video);
    }

    private void validateVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Video file is required");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new ApiException("Video size exceeds 500 MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            throw new ApiException("Unsupported video format. Allowed: MP4, WebM, MKV, AVI, MOV");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".mp4";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}