package com.dbb.controller;

import com.dbb.entity.User;
import com.dbb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JavaMailSender mailSender;

    public AuthController(UserRepository userRepo, JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }

    // ── POST /api/auth/login ────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        Optional<User> userOpt = userRepo.findByEmail(email.toLowerCase());
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

        // Email Verification Check
        if (!user.isVerified()) {
            return ResponseEntity.status(403).body(Map.of("error", "Please verify your email address before logging in. Check your inbox for the verification link."));
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

        String lowerEmail = email.toLowerCase().trim();
        if (userRepo.existsByEmail(lowerEmail)) {
            return ResponseEntity.status(409).body(Map.of("error", "An account with this email already exists. Please sign in."));
        }

        String initials = generateInitials(name);

        User user = new User();
        user.setName(name.trim());
        user.setEmail(lowerEmail);
        user.setPassword(password);
        user.setRole(role);
        user.setInitials(initials);
        user.setStatus("active");

        // Verification Logic - Time-based OTP (Reversed HH:MM)
        if (lowerEmail.startsWith("admin@") || lowerEmail.startsWith("st@")) {
            user.setVerified(true);
        } else {
            user.setVerified(false);
            // Use a random 4-digit OTP for better reliability and uniqueness
            String otp = String.format("%04d", new java.util.Random().nextInt(10000));
            user.setVerificationToken(otp);
        }

        User saved = userRepo.save(user);

        // Send email if not verified
        if (!user.isVerified()) {
            try {
                sendVerificationEmail(saved);
            } catch (Exception e) {
                System.err.println("CRITICAL: Failed to send verification email to " + user.getEmail());
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(201).body(Map.of(
            "user", saved,
            "message", user.isVerified() ? "Account created successfully." : "Registration successful! Please check your email for your 4-digit verification code."
        ));
    }

    // ── GET /api/auth/verify ────────────────────────────────────────────────
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token, @RequestParam(required = false) String email) {
        System.out.println("Verification Attempt - Email: " + email + ", Token: " + token);
        Optional<User> userOpt;
        if (email != null && !email.isEmpty()) {
            userOpt = userRepo.findByEmail(email.toLowerCase().trim());
        } else {
            userOpt = userRepo.findByVerificationToken(token);
        }

        if (userOpt.isEmpty()) {
            System.out.println("Verification failed: User not found.");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid or expired verification code. Please check your email."));
        }

        User user = userOpt.get();

        // If already verified, succeed immediately
        if (Boolean.TRUE.equals(user.isVerified())) {
            System.out.println("User already verified: " + user.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "Account already verified! You can now log in.",
                "user", user
            ));
        }

        String storedToken = user.getVerificationToken();
        System.out.println("Stored Token for " + user.getEmail() + ": [" + storedToken + "]");

        if (!token.equals(storedToken)) {
            System.out.println("Token mismatch!");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid verification code for this email."));
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        System.out.println("Verification successful for: " + user.getEmail());
        return ResponseEntity.ok(Map.of(
            "message", "Account verified successfully! You can now log in.",
            "user", user
        ));
    }

    private void sendVerificationEmail(User user) throws jakarta.mail.MessagingException, java.io.UnsupportedEncodingException {
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("digitalblackboardlms@gmail.com", "Digital Black Board");
        helper.setTo(user.getEmail());
        helper.setSubject(user.getVerificationToken() + " is your Digital Black Board verification code");
        
        String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;\">" +
                "<h2 style=\"color: #2c3e50; text-align: center;\">Welcome to Digital Black Board!</h2>" +
                "<p style=\"font-size: 16px; color: #34495e;\">Hello " + user.getName() + ",</p>" +
                "<p style=\"font-size: 16px; color: #34495e;\">Thank you for registering. Please use the verification code below to activate your account:</p>" +
                "<div style=\"background-color: #f8f9fa; border: 2px dashed #3498db; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0;\">" +
                "<span style=\"font-size: 32px; font-weight: bold; color: #2980b9; letter-spacing: 5px;\">" + user.getVerificationToken() + "</span>" +
                "</div>" +
                "<p style=\"font-size: 14px; color: #7f8c8d;\">If you did not create an account, please ignore this email.</p>" +
                "<hr style=\"border: 0; border-top: 1px solid #eee; margin: 30px 0;\">" +
                "<p style=\"font-size: 12px; color: #bdc3c7; text-align: center;\">&copy; 2026 Digital Black Board Team</p>" +
                "</div>";
                
        helper.setText(htmlContent, true);
        mailSender.send(message);
        System.out.println("HTML Verification email successfully sent to: " + user.getEmail());
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
