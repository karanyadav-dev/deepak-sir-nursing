package com.deepaksir.repository;

import com.deepaksir.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();

    List<Course> findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    List<Course> findByPublishedTrueAndActiveTrueAndIsPremiumTrue();

    List<Course> findByCourseTypeAndPublishedTrueAndActiveTrue(String courseType);

    List<Course> findByPublishedTrueAndActiveTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Course> findByExamIdAndPublishedTrueAndActiveTrue(UUID examId);

    @Query("SELECT c FROM Course c WHERE c.published = true AND c.active = true " +
           "AND LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Course> searchPublishedCourses(@Param("search") String search);

    Optional<Course> findByIdAndPublishedTrueAndActiveTrue(UUID id);

    Optional<Course> findByIdAndActiveTrue(UUID id);

    long countByPublishedTrueAndActiveTrue();

    boolean existsByTitleIgnoreCase(String title);
}