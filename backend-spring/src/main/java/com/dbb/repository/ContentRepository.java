package com.dbb.repository;

import com.dbb.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findAllByOrderByCreatedAtDesc();
    List<Content> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);
}
