package com.dbb.controller;

import com.dbb.entity.Course;
import com.dbb.entity.Enrollment;
import com.dbb.repository.CourseRepository;
import com.dbb.repository.EnrollmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CourseController(CourseRepository courseRepo, EnrollmentRepository enrollRepo) {
        this.courseRepo = courseRepo;
        this.enrollRepo = enrollRepo;
    }

    // ── Helper: map Course entity → frontend JSON shape ─────────────────────
    private Map<String, Object> mapCourse(Course c) {
        List<String> tags = new ArrayList<>();
        if (c.getTags() != null && !c.getTags().isEmpty()) {
            try {
                tags = objectMapper.readValue(c.getTags(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                tags = Arrays.stream(c.getTags().split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            }
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("title", c.getTitle());
        map.put("category", c.getCategory() != null ? c.getCategory() : "");
        map.put("instructor", c.getInstructor() != null ? c.getInstructor() : "");
        map.put("instructorId", c.getInstructorId());
        map.put("description", c.getDescription() != null ? c.getDescription() : "");
        map.put("duration", c.getDuration() != null ? c.getDuration() : "");
        map.put("lessons", c.getLessons() != null ? c.getLessons() : 0);
        map.put("rating", c.getRating() != null ? c.getRating() : 0);
        map.put("students", c.getStudents() != null ? c.getStudents() : 0);
        map.put("status", c.getStatus() != null ? c.getStatus() : "draft");
        map.put("tags", tags);
        map.put("createdBy", c.getCreatedBy());
        map.put("createdAt", c.getCreatedAt());
        return map;
    }

    // ── GET /api/courses ────────────────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAllCourses(
            @RequestParam(required = false) Integer createdBy,
            @RequestParam(required = false) String status) {

        List<Course> courses;
        if (createdBy != null && status != null) {
            courses = courseRepo.findByCreatedByAndStatusOrderByCreatedAtDesc(createdBy, status);
        } else if (createdBy != null) {
            courses = courseRepo.findByCreatedByOrderByCreatedAtDesc(createdBy);
        } else if (status != null) {
            courses = courseRepo.findByStatusOrderByCreatedAtDesc(status);
        } else {
            courses = courseRepo.findAllByOrderByCreatedAtDesc();
        }

        return courses.stream().map(this::mapCourse).collect(Collectors.toList());
    }

    // ── GET /api/courses/{id} ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer id) {
        return courseRepo.findById(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(mapCourse(c)))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Course not found.")));
    }

    // ── POST /api/courses ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        if (title == null || title.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required."));
        }

        String tagsStr = "[]";
        Object tagsObj = body.get("tags");
        if (tagsObj instanceof List) {
            try { tagsStr = objectMapper.writeValueAsString(tagsObj); } catch (Exception ignored) {}
        } else if (tagsObj instanceof String) {
            tagsStr = (String) tagsObj;
        }

        Course course = new Course();
        course.setTitle(title);
        course.setCategory((String) body.getOrDefault("category", "General"));
        course.setInstructor((String) body.getOrDefault("createdByName", ""));
        course.setStatus((String) body.getOrDefault("status", "draft"));
        course.setDescription((String) body.getOrDefault("description", ""));
        course.setDuration((String) body.getOrDefault("duration", ""));
        course.setTags(tagsStr);

        Object createdByObj = body.get("createdBy");
        if (createdByObj instanceof Number) course.setCreatedBy(((Number) createdByObj).intValue());

        Object lessonsObj = body.get("lessons");
        if (lessonsObj instanceof Number) course.setLessons(((Number) lessonsObj).intValue());
        else course.setLessons(0);

        Course saved = courseRepo.save(course);
        return ResponseEntity.status(201).body(mapCourse(saved));
    }

    // ── PUT /api/courses/{id} ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        var opt = courseRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Course not found."));

        Course course = opt.get();

        if (body.containsKey("title")) course.setTitle((String) body.get("title"));
        if (body.containsKey("category")) course.setCategory((String) body.get("category"));
        if (body.containsKey("status")) course.setStatus((String) body.get("status"));
        if (body.containsKey("description")) course.setDescription((String) body.get("description"));
        if (body.containsKey("instructor")) course.setInstructor((String) body.get("instructor"));
        if (body.containsKey("duration")) course.setDuration((String) body.get("duration"));
        if (body.containsKey("lessons")) {
            Object l = body.get("lessons");
            course.setLessons(l instanceof Number ? ((Number) l).intValue() : 0);
        }
        if (body.containsKey("tags")) {
            Object tagsObj = body.get("tags");
            if (tagsObj instanceof List) {
                try { course.setTags(objectMapper.writeValueAsString(tagsObj)); } catch (Exception ignored) {}
            } else if (tagsObj instanceof String) {
                course.setTags((String) tagsObj);
            }
        }

        Course saved = courseRepo.save(course);
        return ResponseEntity.ok(mapCourse(saved));
    }

    // ── DELETE /api/courses/{id} ────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        if (!courseRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Course not found."));
        }
        courseRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Course and related data deleted."));
    }

    // ── POST /api/courses/{id}/enroll ───────────────────────────────────────
    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollStudent(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Object userIdObj = body.get("userId");
        if (userIdObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required."));
        }
        Integer userId = userIdObj instanceof Number ? ((Number) userIdObj).intValue() : Integer.parseInt(userIdObj.toString());

        if (!courseRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Course not found."));
        }

        if (enrollRepo.existsByCourseIdAndUserId(id, userId)) {
            return ResponseEntity.status(409).body(Map.of("error", "Already enrolled."));
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(id);
        enrollment.setUserId(userId);
        enrollRepo.save(enrollment);

        // Increment student count
        courseRepo.findById(id).ifPresent(c -> {
            c.setStudents((c.getStudents() != null ? c.getStudents() : 0) + 1);
            courseRepo.save(c);
        });

        List<Integer> enrolledIds = enrollRepo.findByCourseId(id)
                .stream().map(Enrollment::getUserId).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("message", "Enrolled successfully.", "enrollments", enrolledIds));
    }

    // ── GET /api/courses/{id}/enrollment ────────────────────────────────────
    @GetMapping("/{id}/enrollment")
    public Map<String, Object> getEnrollment(@PathVariable Integer id) {
        List<Integer> enrolled = enrollRepo.findByCourseId(id)
                .stream().map(Enrollment::getUserId).collect(Collectors.toList());
        return Map.of("enrolled", enrolled);
    }
}
