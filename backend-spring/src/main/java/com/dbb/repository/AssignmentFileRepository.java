package com.dbb.repository;

import com.dbb.entity.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Integer> {
    List<AssignmentFile> findByAssignmentId(Integer assignmentId);
    void deleteByAssignmentId(Integer assignmentId);
}
