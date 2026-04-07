package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "Student";

    private String initials = "";
    private String status = "active";

    @Column(name = "joined_date")
    @JsonProperty("joined_date")
    private LocalDate joinedDate;

    @Column(name = "last_active")
    @JsonProperty("last_active")
    private LocalDate lastActive;

    @Column(name = "courses_count")
    @JsonProperty("courses_count")
    private Integer coursesCount = 0;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (joinedDate == null) joinedDate = LocalDate.now();
        if (lastActive == null) lastActive = LocalDate.now();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ── Getters and Setters ─────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonIgnore
    public String getPassword() { return password; }
    @JsonProperty
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public LocalDate getLastActive() { return lastActive; }
    public void setLastActive(LocalDate lastActive) { this.lastActive = lastActive; }

    public Integer getCoursesCount() { return coursesCount; }
    public void setCoursesCount(Integer coursesCount) { this.coursesCount = coursesCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
