package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "user_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_id", nullable = false)
    @JsonProperty("courseId")
    private Integer courseId;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private Integer userId;

    @Column(name = "enrolled_at")
    @JsonProperty("enrolledAt")
    private LocalDateTime enrolledAt;

    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) enrolledAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}
