package com.dbb.repository;

import com.dbb.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByCourseId(Integer courseId);
    boolean existsByCourseIdAndUserId(Integer courseId, Integer userId);
}
