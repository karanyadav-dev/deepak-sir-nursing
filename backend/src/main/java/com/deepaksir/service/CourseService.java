package com.deepaksir.service;

import com.deepaksir.dto.*;
import com.deepaksir.entity.*;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    // ========== GET METHODS ==========

    @Transactional(readOnly = true)
    public List<CourseResponse> getCourses(String search, Boolean premiumOnly, 
                                           String type, Integer year, Pageable pageable) {
        List<Course> courses;

        if (search != null && !search.isEmpty()) {
            courses = courseRepository.searchPublishedCourses(search);
        } else if (premiumOnly != null && premiumOnly) {
            courses = courseRepository.findByPublishedTrueAndActiveTrueAndIsPremiumTrue();
        } else if (type != null && !type.isEmpty()) {
            courses = courseRepository.findByCourseTypeAndPublishedTrueAndActiveTrue(type);
        } else {
            courses = courseRepository.findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
        }

        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(UUID courseId, UUID userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found"));

        CourseDetailResponse response = new CourseDetailResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setPrice(course.getPrice());
        response.setPremium(course.isPremium());
        response.setPublished(course.isPublished());

        if (userId != null) {
            response.setEnrolled(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId));
        }

        response.setChapters(course.getChapters().stream()
                .map(this::mapToChapterResponse)
                .collect(Collectors.toList()));

        return response;
    }

    // ========== ADMIN CRUD ==========

    @Transactional
    public CourseResponse createCourse(CourseRequest request, MultipartFile thumbnail) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);
        course.setPremium(request.isPremium());
        course.setPublished(request.getPublished());
        course.setCourseType(request.getCourseType());
        course.setDuration(request.getDuration());

        // Thumbnail upload
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storeThumbnail(thumbnail);
            course.setThumbnailUrl(thumbnailUrl);
        }

        if (request.getExamId() != null) {
            Exam exam = examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new ApiException("Exam not found"));
            course.setExam(exam);
        }

        Course saved = courseRepository.save(course);
        return mapToCourseResponse(saved);
    }

    @Transactional
    public CourseResponse updateCourse(UUID courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found"));

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getPublished() != null) course.setPublished(request.getPublished());
        if (request.getCourseType() != null) course.setCourseType(request.getCourseType());
        if (request.getDuration() != null) course.setDuration(request.getDuration());

        Course updated = courseRepository.save(course);
        return mapToCourseResponse(updated);
    }

    @Transactional
    public void deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found"));
        course.setActive(false);
        courseRepository.save(course);
    }

    // ========== ENROLLMENT ==========

    @Transactional
    public EnrollmentResponse enrollCourse(UUID courseId, UUID userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ApiException("Already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollment.setProgressPercentage(0.0);

        Enrollment saved = enrollmentRepository.save(enrollment);

        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(saved.getId());
        response.setUserId(userId);
        response.setCourseId(courseId);
        response.setCourseTitle(course.getTitle());
        response.setEnrolledAt(saved.getEnrolledAt());
        response.setStatus(saved.getStatus().name());
        return response;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getUserEnrolledCourses(UUID userId) {
        return enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId)
                .stream()
                .map(enrollment -> mapToCourseResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }

    // ========== HELPER METHODS ==========

    private String storeThumbnail(MultipartFile file) {
        try {
            Path uploadDir = Paths.get("./uploads/course-thumbnails");
            Files.createDirectories(uploadDir);
            String filename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return "/api/files/course-thumbnails/" + filename;
        } catch (IOException e) {
            throw new ApiException("Failed to upload thumbnail: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setPrice(course.getPrice());
        response.setPremium(course.isPremium());
        response.setPublished(course.isPublished());
        response.setCourseType(course.getCourseType());
        response.setDuration(course.getDuration());

        if (course.getExam() != null) {
            response.setExamId(course.getExam().getId());
            response.setExamName(course.getExam().getName());
        }

        response.setChapterCount(course.getChapters().size());
        response.setLessonCount(course.getChapters().stream()
                .mapToInt(ch -> ch.getLessons().size()).sum());
        return response;
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        ChapterResponse response = new ChapterResponse();
        response.setId(chapter.getId());
        response.setTitle(chapter.getTitle());
        response.setDescription(chapter.getDescription());
        response.setOrderIndex(chapter.getOrderIndex());
        response.setLessons(chapter.getLessons().stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setDescription(lesson.getDescription());
        response.setVideoUrl(lesson.getVideoUrl());
        response.setOrderIndex(lesson.getOrderIndex());
        response.setDurationMinutes(lesson.getDurationMinutes());
        response.setPreview(lesson.isPreview());
        return response;
    }
}
