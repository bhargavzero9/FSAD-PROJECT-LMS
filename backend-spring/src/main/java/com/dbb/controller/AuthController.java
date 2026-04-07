package com.dbb.controller;

import com.dbb.entity.User;
import com.dbb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ── POST /api/auth/login ────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        var userOpt = userRepo.findByEmail(email.toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "No account found with that email address."));
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect password. Please try again."));
        }

        if ("inactive".equals(user.getStatus())) {
            return ResponseEntity.status(403).body(Map.of("error", "Your account is deactivated. Please contact support."));
        }

        // Update last_active
        user.setLastActive(LocalDate.now());
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("user", user));
    }

    // ── POST /api/auth/register ─────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        if (name == null || email == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required."));
        }

        if (userRepo.existsByEmail(email.toLowerCase())) {
            return ResponseEntity.status(409).body(Map.of("error", "An account with this email already exists. Please sign in."));
        }

        String initials = generateInitials(name);

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.toLowerCase().trim());
        user.setPassword(password);
        user.setRole(role);
        user.setInitials(initials);
        user.setStatus("active");

        User saved = userRepo.save(user);
        return ResponseEntity.status(201).body(Map.of("user", saved));
    }

    private String generateInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0)));
        }
        return sb.length() > 2 ? sb.substring(0, 2) : sb.toString();
    }
}
