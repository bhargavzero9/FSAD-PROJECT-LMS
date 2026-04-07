package com.dbb.controller;

import com.dbb.entity.Announcement;
import com.dbb.repository.AnnouncementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepo;

    public AnnouncementController(AnnouncementRepository announcementRepo) {
        this.announcementRepo = announcementRepo;
    }

    // ── Helper: map to frontend shape ───────────────────────────────────────
    private Map<String, Object> mapAnnouncement(Announcement a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("message", a.getContent());
        map.put("priority", a.getPriority());
        map.put("type", a.getPriority());
        map.put("author", a.getAuthor());
        map.put("date", a.getDate());
        return map;
    }

    // ── GET /api/announcements ──────────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return announcementRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapAnnouncement).collect(Collectors.toList());
    }

    // ── POST /api/announcements ─────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String message = (String) body.get("message");

        if (title == null || message == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title and message are required."));
        }

        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(message);
        a.setPriority((String) body.getOrDefault("priority", "normal"));
        a.setAuthor((String) body.getOrDefault("author", "System"));

        Object authorIdObj = body.get("authorId");
        if (authorIdObj instanceof Number) a.setAuthorId(((Number) authorIdObj).intValue());

        Announcement saved = announcementRepo.save(a);
        return ResponseEntity.status(201).body(mapAnnouncement(saved));
    }

    // ── DELETE /api/announcements/{id} ──────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (!announcementRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Announcement not found."));
        }
        announcementRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Announcement deleted."));
    }
}
