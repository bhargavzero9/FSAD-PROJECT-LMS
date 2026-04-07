package com.dbb.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    // ── GET /api/health ─────────────────────────────────────────────────────
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", "ok");
        map.put("database", "connected");
        map.put("timestamp", LocalDateTime.now().toString());
        return map;
    }
}
