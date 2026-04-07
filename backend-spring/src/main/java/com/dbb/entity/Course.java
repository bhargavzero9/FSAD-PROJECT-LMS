package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String category = "";
    private String instructor = "";

    @Column(name = "instructor_id")
    @JsonProperty("instructorId")
    private Integer instructorId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String duration = "";
    private Integer lessons = 0;
    private BigDecimal rating = BigDecimal.ZERO;
    private Integer students = 0;
    private String status = "draft";

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(name = "created_by")
    @JsonProperty("createdBy")
    private Integer createdBy;

    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ── Getters and Setters ─────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getLessons() { return lessons; }
    public void setLessons(Integer lessons) { this.lessons = lessons; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getStudents() { return students; }
    public void setStudents(Integer students) { this.students = students; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
