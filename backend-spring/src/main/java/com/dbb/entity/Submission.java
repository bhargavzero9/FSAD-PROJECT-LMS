package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "submissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
})
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assignment_id", nullable = false)
    @JsonProperty("assignmentId")
    private Integer assignmentId;

    @Column(name = "student_id", nullable = false)
    @JsonProperty("studentId")
    private Integer studentId;

    @Column(name = "student_name")
    @JsonProperty("studentName")
    private String studentName = "";

    @Column(name = "student_initials")
    @JsonProperty("studentInitials")
    private String studentInitials = "";

    @Column(name = "assignment_title")
    @JsonProperty("assignmentTitle")
    private String assignmentTitle = "";

    @Column(name = "course_name")
    @JsonProperty("courseName")
    private String courseName = "";

    @Column(name = "course_id")
    @JsonProperty("courseId")
    private Integer courseId;

    @Column(name = "max_score")
    @JsonProperty("maxScore")
    private Integer maxScore = 100;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "submitted_at")
    @JsonProperty("submittedAt")
    private LocalDate submittedAt;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private String status = "pending";

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) submittedAt = LocalDate.now();
    }

    // ── Getters and Setters ─────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentInitials() { return studentInitials; }
    public void setStudentInitials(String studentInitials) { this.studentInitials = studentInitials; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public LocalDate getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDate submittedAt) { this.submittedAt = submittedAt; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
