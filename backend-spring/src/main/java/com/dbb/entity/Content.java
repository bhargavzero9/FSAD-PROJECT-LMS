package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String type = "document";

    @Column(name = "course_id")
    @JsonProperty("courseId")
    private Integer courseId;

    @Column(name = "course_name")
    @JsonProperty("courseName")
    private String courseName = "";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_body", columnDefinition = "TEXT")
    @JsonProperty("content")
    private String contentBody;

    private String url = "";

    @Column(name = "created_by")
    @JsonProperty("createdBy")
    private Integer createdBy;

    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDate.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContentBody() { return contentBody; }
    public void setContentBody(String contentBody) { this.contentBody = contentBody; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
