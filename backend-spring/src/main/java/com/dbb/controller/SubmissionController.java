package com.dbb.controller;

import com.dbb.entity.Assignment;
import com.dbb.entity.Submission;
import com.dbb.entity.SubmissionFile;
import com.dbb.repository.AssignmentRepository;
import com.dbb.repository.SubmissionFileRepository;
import com.dbb.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionRepository subRepo;
    private final SubmissionFileRepository fileRepo;
    private final AssignmentRepository assignmentRepo;

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    public SubmissionController(SubmissionRepository subRepo,
                                SubmissionFileRepository fileRepo,
                                AssignmentRepository assignmentRepo) {
        this.subRepo = subRepo;
        this.fileRepo = fileRepo;
        this.assignmentRepo = assignmentRepo;
    }

    // ── Helper: build response with files ───────────────────────────────────
    private Map<String, Object> buildResponse(Submission s) {
        List<SubmissionFile> files = fileRepo.findBySubmissionId(s.getId());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getId());
        map.put("assignmentId", s.getAssignmentId());
        map.put("studentId", s.getStudentId());
        map.put("studentName", s.getStudentName());
        map.put("studentInitials", s.getStudentInitials());
        map.put("assignmentTitle", s.getAssignmentTitle());
        map.put("courseName", s.getCourseName());
        map.put("courseId", s.getCourseId());
        map.put("maxScore", s.getMaxScore());
        map.put("answer", s.getAnswer());
        map.put("submittedAt", s.getSubmittedAt());
        map.put("score", s.getScore());
        map.put("feedback", s.getFeedback());
        map.put("status", s.getStatus());
        map.put("files", files);
        return map;
    }

    // ── GET /api/submissions ────────────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAllSubmissions(
            @RequestParam(required = false) Integer assignmentId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId) {

        List<Submission> submissions;
        if (assignmentId != null) {
            submissions = subRepo.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId);
        } else if (studentId != null) {
            submissions = subRepo.findByStudentIdOrderBySubmittedAtDesc(studentId);
        } else if (courseId != null) {
            submissions = subRepo.findByCourseIdOrderBySubmittedAtDesc(courseId);
        } else {
            submissions = subRepo.findAllByOrderBySubmittedAtDesc();
        }
        return submissions.stream().map(this::buildResponse).collect(Collectors.toList());
    }

    // ── GET /api/submissions/admin/{adminId} ────────────────────────────────
    @GetMapping("/admin/{adminId}")
    public List<Map<String, Object>> getAdminSubmissions(@PathVariable Integer adminId) {
        return subRepo.findByAdminCourses(adminId)
                .stream().map(this::buildResponse).collect(Collectors.toList());
    }

    // ── GET /api/submissions/{id} ───────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubmission(@PathVariable Integer id) {
        return subRepo.findById(id)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(buildResponse(s)))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Submission not found.")));
    }

    // ── POST /api/submissions (multipart) ───────────────────────────────────
    @PostMapping
    public ResponseEntity<?> submitAssignment(
            @RequestParam("assignmentId") Integer assignmentId,
            @RequestParam("studentId") Integer studentId,
            @RequestParam(required = false, defaultValue = "") String studentName,
            @RequestParam(required = false, defaultValue = "") String studentInitials,
            @RequestParam(required = false, defaultValue = "") String answer,
            @RequestParam(required = false) MultipartFile[] files) {

        if (assignmentId == null || studentId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "assignmentId and studentId are required."));
        }

        // Check for duplicate
        if (subRepo.findByAssignmentIdAndStudentId(assignmentId, studentId).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "You have already submitted this assignment."));
        }

        // Get assignment info
        Assignment assignment = assignmentRepo.findById(assignmentId).orElse(null);

        Submission sub = new Submission();
        sub.setAssignmentId(assignmentId);
        sub.setStudentId(studentId);
        sub.setStudentName(studentName);
        sub.setStudentInitials(studentInitials);
        sub.setAssignmentTitle(assignment != null ? assignment.getTitle() : "");
        sub.setCourseName(assignment != null ? assignment.getCourseName() : "");
        sub.setCourseId(assignment != null ? assignment.getCourseId() : null);
        sub.setMaxScore(assignment != null ? assignment.getMaxScore() : 100);
        sub.setAnswer(answer);
        sub.setStatus("pending");

        Submission saved = subRepo.save(sub);

        // Save uploaded files
        if (files != null && files.length > 0) {
            try {
                Path subDir = Paths.get(uploadDir, "submissions");
                java.nio.file.Files.createDirectories(subDir);

                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;
                    String uniqueName = "submission-" + System.currentTimeMillis() + "-" +
                            (int)(Math.random() * 1_000_000_000) +
                            getExtension(file.getOriginalFilename());

                    Path destPath = subDir.resolve(uniqueName);
                    file.transferTo(destPath.toFile());

                    SubmissionFile sf = new SubmissionFile();
                    sf.setSubmissionId(saved.getId());
                    sf.setOriginalName(file.getOriginalFilename());
                    sf.setStoredName(uniqueName);
                    sf.setMimetype(file.getContentType());
                    sf.setSize(file.getSize());
                    sf.setUrl("/uploads/submissions/" + uniqueName);
                    fileRepo.save(sf);
                }
            } catch (IOException e) {
                // Files failed but submission was created
            }
        }

        // Update assignment submission count
        if (assignment != null) {
            assignment.setSubmissions((assignment.getSubmissions() != null ? assignment.getSubmissions() : 0) + 1);
            assignmentRepo.save(assignment);
        }

        return ResponseEntity.status(201).body(buildResponse(saved));
    }

    // ── PUT /api/submissions/{id}/grade ─────────────────────────────────────
    @PutMapping("/{id}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        var opt = subRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Submission not found."));

        Submission sub = opt.get();
        boolean wasGraded = "graded".equals(sub.getStatus());

        Object scoreObj = body.get("score");
        if (scoreObj instanceof Number) sub.setScore(((Number) scoreObj).intValue());
        sub.setFeedback((String) body.getOrDefault("feedback", ""));
        sub.setStatus("graded");

        subRepo.save(sub);

        if (!wasGraded) {
            assignmentRepo.findById(sub.getAssignmentId()).ifPresent(a -> {
                a.setGraded((a.getGraded() != null ? a.getGraded() : 0) + 1);
                assignmentRepo.save(a);
            });
        }

        return ResponseEntity.ok(buildResponse(sub));
    }

    // ── DELETE /api/submissions/{id} ────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable Integer id) {
        // Clean up files
        List<SubmissionFile> files = fileRepo.findBySubmissionId(id);
        for (SubmissionFile f : files) {
            try {
                Path filePath = Paths.get(uploadDir, "submissions", f.getStoredName());
                java.nio.file.Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }

        subRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Submission deleted."));
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
