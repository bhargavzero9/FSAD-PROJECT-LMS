package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_files")
public class AssignmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assignment_id", nullable = false)
    @JsonProperty("assignmentId")
    private Integer assignmentId;

    @Column(name = "original_name", nullable = false)
    @JsonProperty("originalName")
    private String originalName;

    @Column(name = "stored_name", nullable = false)
    @JsonProperty("storedName")
    private String storedName;

    @JsonProperty("mimetype")
    private String mimetype = "";

    private Long size = 0L;

    @Column(name = "uploaded_by")
    @JsonProperty("uploadedBy")
    private Integer uploadedBy;

    @Column(name = "uploaded_by_name")
    @JsonProperty("uploadedByName")
    private String uploadedByName = "";

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

    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }

    public String getMimetype() { return mimetype; }
    public void setMimetype(String mimetype) { this.mimetype = mimetype; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public Integer getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Integer uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
