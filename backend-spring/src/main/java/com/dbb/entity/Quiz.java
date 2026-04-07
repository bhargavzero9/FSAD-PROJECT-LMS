package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(name = "course_id")
    @JsonProperty("courseId")
    private Integer courseId;

    @Column(name = "created_by")
    @JsonProperty("createdBy")
    private Integer createdBy;

    @Column(name = "allow_retake")
    @JsonProperty("allowRetake")
    private Boolean allowRetake = true;

    @Column(name = "time_limit")
    @JsonProperty("timeLimit")
    private Integer timeLimit = 0;

    @Column(name = "due_date")
    @JsonProperty("dueDate")
    private String dueDate;

    @Column(columnDefinition = "JSON")
    private String questions;

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

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Boolean getAllowRetake() { return allowRetake; }
    public void setAllowRetake(Boolean allowRetake) { this.allowRetake = allowRetake; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getQuestions() { return questions; }
    public void setQuestions(String questions) { this.questions = questions; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
