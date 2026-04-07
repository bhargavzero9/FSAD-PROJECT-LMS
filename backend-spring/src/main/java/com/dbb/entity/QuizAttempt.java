package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quiz_id", nullable = false)
    @JsonProperty("quizId")
    private Integer quizId;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private Integer userId;

    private Integer score = 0;
    private Integer total = 0;

    private LocalDate date;

    @PrePersist
    protected void onCreate() {
        if (date == null) date = LocalDate.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
