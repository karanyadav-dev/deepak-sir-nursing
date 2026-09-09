package com.deepaksir.service;

import com.deepaksir.entity.*;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassProgressService {

    private final ClassProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ClassProgress saveProgress(UUID lessonId, int watchedSeconds, int totalSeconds, boolean completed) {
        UUID userId = getCurrentUserId();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException("Lesson not found"));

        Course course = lesson.getChapter().getCourse();

        ClassProgress progress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    ClassProgress newProgress = new ClassProgress();
                    newProgress.setUser(userRepository.findById(userId).orElseThrow());
                    newProgress.setLesson(lesson);
                    newProgress.setCourse(course);
                    return newProgress;
                });

        progress.setWatchedSeconds(watchedSeconds);
        progress.setTotalSeconds(totalSeconds);
        progress.setCompleted(completed);
        progress.setLastPositionSeconds(watchedSeconds);
        progress.setLastWatchedAt(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public ClassProgress getProgress(UUID lessonId) {
        UUID userId = getCurrentUserId();
        return progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ClassProgress> getCourseProgress(UUID courseId) {
        UUID userId = getCurrentUserId();
        return progressRepository.findByUserIdAndCourseId(userId, courseId);
    }

    @Transactional(readOnly = true)
    public double calculateCourseProgress(UUID courseId) {
        UUID userId = getCurrentUserId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found"));

        int totalLessons = course.getChapters().stream()
                .mapToInt(ch -> ch.getLessons().size())
                .sum();

        if (totalLessons == 0) return 0;

        long completedLessons = progressRepository
                .countByUserIdAndCourseIdAndCompletedTrue(userId, courseId);

        return (double) completedLessons / totalLessons * 100;
    }

    @Transactional(readOnly = true)
    public List<ClassProgress> getContinueWatching() {
        UUID userId = getCurrentUserId();
        return progressRepository.findByUserIdOrderByLastWatchedAtDesc(userId)
                .stream()
                .filter(p -> !p.isCompleted())
                .limit(10)
                .collect(Collectors.toList());
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ApiException("Not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
        return user.getId();
    }
}