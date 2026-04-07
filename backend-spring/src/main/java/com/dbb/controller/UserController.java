package com.dbb.controller;

import com.dbb.entity.User;
import com.dbb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ── GET /api/users ──────────────────────────────────────────────────────
    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // ── GET /api/users/{id} ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found.")));
    }

    // ── POST /api/users ─────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        if (name == null || email == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required."));
        }

        if (userRepo.existsByEmail(email.toLowerCase())) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use."));
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
        return ResponseEntity.status(201).body(saved);
    }

    // ── PUT /api/users/{id} ─────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        var opt = userRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found."));

        User user = opt.get();

        if (body.containsKey("name")) {
            user.setName(body.get("name"));
            user.setInitials(generateInitials(body.get("name")));
        }
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("role")) user.setRole(body.get("role"));
        if (body.containsKey("status")) user.setStatus(body.get("status"));
        if (body.containsKey("password")) user.setPassword(body.get("password"));
        if (body.containsKey("initials")) user.setInitials(body.get("initials"));

        User saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    // ── DELETE /api/users/{id} ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted."));
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
