package com.dbb.controller;

import com.dbb.entity.Content;
import com.dbb.repository.ContentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentRepository contentRepo;

    public ContentController(ContentRepository contentRepo) {
        this.contentRepo = contentRepo;
    }

    // ── GET /api/content ────────────────────────────────────────────────────
    @GetMapping
    public List<Content> getAll(@RequestParam(required = false) Integer createdBy) {
        if (createdBy != null) {
            return contentRepo.findByCreatedByOrderByCreatedAtDesc(createdBy);
        }
        return contentRepo.findAllByOrderByCreatedAtDesc();
    }

    // ── POST /api/content ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Content c = new Content();
        c.setTitle((String) body.getOrDefault("title", ""));
        c.setType((String) body.getOrDefault("type", "document"));
        c.setCourseName((String) body.getOrDefault("courseName", ""));
        c.setDescription((String) body.getOrDefault("description", ""));
        c.setContentBody((String) body.getOrDefault("content", ""));
        c.setUrl((String) body.getOrDefault("url", ""));

        Object courseIdObj = body.get("courseId");
        if (courseIdObj instanceof Number) c.setCourseId(((Number) courseIdObj).intValue());

        Object createdByObj = body.get("createdBy");
        if (createdByObj instanceof Number) c.setCreatedBy(((Number) createdByObj).intValue());

        Content saved = contentRepo.save(c);
        return ResponseEntity.status(201).body(saved);
    }

    // ── PUT /api/content/{id} ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        var opt = contentRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Content not found."));

        Content c = opt.get();
        if (body.containsKey("title")) c.setTitle((String) body.get("title"));
        if (body.containsKey("type")) c.setType((String) body.get("type"));
        if (body.containsKey("description")) c.setDescription((String) body.get("description"));
        if (body.containsKey("content")) c.setContentBody((String) body.get("content"));
        if (body.containsKey("url")) c.setUrl((String) body.get("url"));

        Content saved = contentRepo.save(c);
        return ResponseEntity.ok(saved);
    }

    // ── DELETE /api/content/{id} ────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (!contentRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Content not found."));
        }
        contentRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Content deleted."));
    }
}
