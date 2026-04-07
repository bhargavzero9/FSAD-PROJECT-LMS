package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission_files")
public class SubmissionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "submission_id", nullable = false)
    @JsonProperty("submissionId")
    private Integer submissionId;

    @Column(name = "original_name", nullable = false)
    @JsonProperty("originalName")
    private String originalName;

    @Column(name = "stored_name", nullable = false)
    @JsonProperty("storedName")
    private String storedName;

    @JsonProperty("mimetype")
    private String mimetype = "";

    private Long size = 0L;

    private String url = "";

    @Column(name = "uploaded_at")
    @JsonProperty("uploadedAt")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSubmissionId() { return submissionId; }
    public void setSubmissionId(Integer submissionId) { this.submissionId = submissionId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }

    public String getMimetype() { return mimetype; }
    public void setMimetype(String mimetype) { this.mimetype = mimetype; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
