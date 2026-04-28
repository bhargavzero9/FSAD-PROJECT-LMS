package com.dbb.controller;

import com.dbb.entity.Assignment;
import com.dbb.entity.AssignmentFile;
import com.dbb.entity.Course;
import com.dbb.repository.AssignmentFileRepository;
import com.dbb.repository.AssignmentRepository;
import com.dbb.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepo;
    private final AssignmentFileRepository fileRepo;
    private final CourseRepository courseRepo;

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    public AssignmentController(AssignmentRepository assignmentRepo,
                                AssignmentFileRepository fileRepo,
                                CourseRepository courseRepo) {
        this.assignmentRepo = assignmentRepo;
        this.fileRepo = fileRepo;
        this.courseRepo = courseRepo;
    }

    // ── Helper: build assignment response with files ────────────────────────
    private Map<String, Object> buildResponse(Assignment a) {
        List<AssignmentFile> files = fileRepo.findByAssignmentId(a.getId());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("courseId", a.getCourseId());
        map.put("courseName", a.getCourseName());
        map.put("createdBy", a.getCreatedBy());
        map.put("dueDate", a.getDueDate());
        map.put("maxScore", a.getMaxScore());
        map.put("description", a.getDescription());
        map.put("submissions", a.getSubmissions());
        map.put("graded", a.getGraded());
        map.put("status", a.getStatus());
        map.put("createdAt", a.getCreatedAt());
        map.put("files", files);
        return map;
    }

    // ── GET /api/assignments ────────────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAllAssignments(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer createdBy) {

        List<Assignment> assignments;
        if (courseId != null) {
            assignments = assignmentRepo.findByCourseIdOrderByCreatedAtDesc(courseId);
        } else if (createdBy != null) {
            assignments = assignmentRepo.findByCreatedByThroughCourse(createdBy);
        } else {
            assignments = assignmentRepo.findAllByOrderByCreatedAtDesc();
        }
        return assignments.stream().map(this::buildResponse).collect(Collectors.toList());
    }

    // ── GET /api/assignments/student/{studentId} ────────────────────────────
    @GetMapping("/student/{studentId}")
    public List<Map<String, Object>> getStudentAssignments(@PathVariable Integer studentId) {
        return assignmentRepo.findByStudentEnrollment(studentId)
                .stream().map(this::buildResponse).collect(Collectors.toList());
    }

    // ── GET /api/assignments/{id} ───────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable Integer id) {
        return assignmentRepo.findById(id)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(buildResponse(a)))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Assignment not found.")));
    }

    // ── POST /api/assignments ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        if (title == null || title.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required."));
        }

        Object courseIdObj = body.get("courseId");
        if (courseIdObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Course is required."));
        }
        Integer courseId = courseIdObj instanceof Number ? ((Number) courseIdObj).intValue() : Integer.parseInt(courseIdObj.toString());

        // Get course name
        String courseName = courseRepo.findById(courseId).map(Course::getTitle).orElse("");

        Assignment a = new Assignment();
        a.setTitle(title);
        a.setCourseId(courseId);
        a.setCourseName(courseName);
        a.setDueDate((String) body.getOrDefault("dueDate", ""));
        a.setDescription((String) body.getOrDefault("description", ""));
        a.setStatus("active");

        Object createdByObj = body.get("createdBy");
        if (createdByObj instanceof Number) a.setCreatedBy(((Number) createdByObj).intValue());

        Object maxScoreObj = body.get("maxScore");
        a.setMaxScore(maxScoreObj instanceof Number ? ((Number) maxScoreObj).intValue() : 100);

        Assignment saved = assignmentRepo.save(a);
        return ResponseEntity.status(201).body(buildResponse(saved));
    }

    // ── PUT /api/assignments/{id} ───────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        var opt = assignmentRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Assignment not found."));

        Assignment a = opt.get();
        if (body.containsKey("title")) a.setTitle((String) body.get("title"));
        if (body.containsKey("dueDate")) a.setDueDate((String) body.get("dueDate"));
        if (body.containsKey("description")) a.setDescription((String) body.get("description"));
        if (body.containsKey("status")) a.setStatus((String) body.get("status"));
        if (body.containsKey("maxScore")) {
            Object ms = body.get("maxScore");
            a.setMaxScore(ms instanceof Number ? ((Number) ms).intValue() : 100);
        }

        Assignment saved = assignmentRepo.save(a);
        return ResponseEntity.ok(buildResponse(saved));
    }

    // ── DELETE /api/assignments/{id} ────────────────────────────────────────
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteAssignment(@PathVariable Integer id) {
        // Clean up files on disk
        List<AssignmentFile> files = fileRepo.findByAssignmentId(id);
        for (AssignmentFile f : files) {
            try {
                Path filePath = Paths.get(uploadDir, "assignments", f.getStoredName());
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
        fileRepo.deleteByAssignmentId(id);
        assignmentRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Assignment deleted."));
    }

    // ── POST /api/assignments/{id}/files ────────────────────────────────────
    @PostMapping("/{id}/files")
    public ResponseEntity<?> uploadFiles(
            @PathVariable Integer id,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) Integer uploadedBy,
            @RequestParam(required = false, defaultValue = "Unknown") String uploadedByName,
            @RequestParam(required = false, defaultValue = "Assignment") String assignmentTitle) {

        // Auto-create assignment if needed
        if (!assignmentRepo.existsById(id)) {
            Assignment a = new Assignment();
            a.setId(id);
            a.setTitle(assignmentTitle);
            a.setCreatedBy(uploadedBy);
            a.setStatus("active");
            assignmentRepo.save(a);
        }

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "No files uploaded."));
        }

        try {
            Path assignmentDir = Paths.get(uploadDir, "assignments");
            java.nio.file.Files.createDirectories(assignmentDir);

            for (MultipartFile file : files) {
                String uniqueName = "assignment-" + System.currentTimeMillis() + "-" +
                        (int)(Math.random() * 1_000_000_000) +
                        getExtension(file.getOriginalFilename());

                Path destPath = assignmentDir.resolve(uniqueName);
                file.transferTo(destPath.toFile());

                AssignmentFile af = new AssignmentFile();
                af.setAssignmentId(id);
                af.setOriginalName(file.getOriginalFilename());
                af.setStoredName(uniqueName);
                af.setMimetype(file.getContentType());
                af.setSize(file.getSize());
                af.setUploadedBy(uploadedBy);
                af.setUploadedByName(uploadedByName);
                af.setUrl("/api/assignments/files/" + uniqueName);
                fileRepo.save(af);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload files."));
        }

        Assignment assignment = assignmentRepo.findById(id).orElse(null);
        return ResponseEntity.status(201).body(Map.of(
            "message", "Files uploaded.",
            "assignment", assignment != null ? buildResponse(assignment) : Map.of()
        ));
    }

    // ── DELETE /api/assignments/{id}/files/{fileId} ─────────────────────────
    @DeleteMapping("/{id}/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer id, @PathVariable Integer fileId) {
        var opt = fileRepo.findById(fileId);
        if (opt.isEmpty() || !opt.get().getAssignmentId().equals(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "File not found."));
        }

        AssignmentFile af = opt.get();
        try {
            Path filePath = Paths.get(uploadDir, "assignments", af.getStoredName());
            java.nio.file.Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}

        fileRepo.deleteById(fileId);

        Assignment assignment = assignmentRepo.findById(id).orElse(null);
        return ResponseEntity.ok(Map.of(
            "message", "File deleted.",
            "assignment", assignment != null ? buildResponse(assignment) : Map.of()
        ));
    }

    // ── GET /api/assignments/files/{uniqueName} ─────────────────────────────
    @GetMapping("/files/{uniqueName}")
    public ResponseEntity<?> serveFile(@PathVariable String uniqueName) {
        try {
            Path filePath = Paths.get(uploadDir, "assignments", uniqueName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body(Map.of("error", "File not found."));
            }

            // Find original name in DB if possible
            String originalName = fileRepo.findAll().stream()
                    .filter(f -> f.getStoredName().equals(uniqueName))
                    .map(AssignmentFile::getOriginalName)
                    .findFirst().orElse(uniqueName);

            byte[] contents = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "attachment; filename=\"" + originalName + "\"")
                    .body(contents);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error reading file."));
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
