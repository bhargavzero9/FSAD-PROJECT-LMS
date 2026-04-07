package com.dbb.repository;

import com.dbb.entity.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Integer> {
    List<SubmissionFile> findBySubmissionId(Integer submissionId);
}
