package com.dbb.repository;

import com.dbb.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    List<Submission> findAllByOrderBySubmittedAtDesc();
    List<Submission> findByAssignmentIdOrderBySubmittedAtDesc(Integer assignmentId);
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Integer studentId);
    List<Submission> findByCourseIdOrderBySubmittedAtDesc(Integer courseId);
    Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    @Query("SELECT s FROM Submission s WHERE s.assignmentId IN " +
           "(SELECT a.id FROM Assignment a WHERE a.courseId IN " +
           "(SELECT c.id FROM Course c WHERE c.createdBy = :adminId)) ORDER BY s.submittedAt DESC")
    List<Submission> findByAdminCourses(@Param("adminId") Integer adminId);
}
