package com.dbb.repository;

import com.dbb.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findAllByOrderByCreatedAtDesc();
    List<Assignment> findByCourseIdOrderByCreatedAtDesc(Integer courseId);

    @Query("SELECT a FROM Assignment a WHERE a.courseId IN (SELECT c.id FROM Course c WHERE c.createdBy = :createdBy) ORDER BY a.createdAt DESC")
    List<Assignment> findByCreatedByThroughCourse(@Param("createdBy") Integer createdBy);

    @Query("SELECT a FROM Assignment a WHERE a.courseId IN (SELECT e.courseId FROM Enrollment e WHERE e.userId = :studentId) ORDER BY a.createdAt DESC")
    List<Assignment> findByStudentEnrollment(@Param("studentId") Integer studentId);
}
