package com.deepaksir.controller;

import com.deepaksir.dto.*;
import com.deepaksir.entity.Course;
import com.deepaksir.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean premiumOnly,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {
        
        List<CourseResponse> courses = courseService.getCourses(search, premiumOnly, type, year, pageable);
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = userDetails != null ? UUID.fromString(userDetails.getUsername()) : null;
        CourseDetailResponse course = courseService.getCourseById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", course));
    }

    // ========== ADMIN ENDPOINTS ==========

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false, defaultValue = "0") String price,
            @RequestParam(value = "isPremium", required = false, defaultValue = "false") boolean isPremium,
            @RequestParam(value = "published", required = false, defaultValue = "false") boolean published,
            @RequestParam(value = "courseType", required = false) String courseType,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {

        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setPrice(new java.math.BigDecimal(price));
        request.setPremium(isPremium);
        request.setPublished(published);
        request.setCourseType(courseType);
        request.setDuration(duration);

        CourseResponse created = courseService.createCourse(request, thumbnail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request) {
        
        CourseResponse updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }

    // ========== ENROLLMENT ENDPOINTS ==========

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        EnrollmentResponse enrollment = courseService.enrollCourse(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Enrolled successfully", enrollment));
    }

    @GetMapping("/enrollments/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<CourseResponse> courses = courseService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(ApiResponse.success("My courses retrieved", courses));
    }
}