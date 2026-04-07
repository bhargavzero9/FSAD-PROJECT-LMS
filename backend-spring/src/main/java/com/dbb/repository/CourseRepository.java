package com.dbb.repository;

import com.dbb.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);
    List<Course> findByStatusOrderByCreatedAtDesc(String status);
    List<Course> findByCreatedByAndStatusOrderByCreatedAtDesc(Integer createdBy, String status);
    List<Course> findAllByOrderByCreatedAtDesc();
}
