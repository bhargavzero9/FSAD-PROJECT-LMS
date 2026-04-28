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
    private String avatar = "";
    private String bio = "";
    private String timezone = "UTC+5:30";
    private String language = "English";

    @Column(name = "name_changed")
    @JsonProperty("name_changed")
    private Boolean nameChanged = false;

    // Notification Preferences
    private Boolean emailNotifications = true;
    private Boolean pushNotifications = true;
    private Boolean weeklyReports = false;
    private Boolean twoFactor = false;

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

    @Column(name = "is_verified")
    @JsonProperty("is_verified")
    private Boolean isVerified = false;

    @Column(name = "verification_token")
    @JsonIgnore
    private String verificationToken;

    @PrePersist
    protected void onCreate() {
        if (joinedDate == null) joinedDate = LocalDate.now();
        if (lastActive == null) lastActive = LocalDate.now();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (avatar == null) avatar = "";
        if (bio == null) bio = "";
        if (timezone == null) timezone = "UTC+5:30";
        if (language == null) language = "English";
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

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Boolean getNameChanged() { return nameChanged != null && nameChanged; }
    public void setNameChanged(Boolean nameChanged) { this.nameChanged = nameChanged; }

    public Boolean getEmailNotifications() { return emailNotifications != null && emailNotifications; }
    public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public Boolean getPushNotifications() { return pushNotifications != null && pushNotifications; }
    public void setPushNotifications(Boolean pushNotifications) { this.pushNotifications = pushNotifications; }

    public Boolean getWeeklyReports() { return weeklyReports != null && weeklyReports; }
    public void setWeeklyReports(Boolean weeklyReports) { this.weeklyReports = weeklyReports; }

    public Boolean getTwoFactor() { return twoFactor != null && twoFactor; }
    public void setTwoFactor(Boolean twoFactor) { this.twoFactor = twoFactor; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public LocalDate getLastActive() { return lastActive; }
    public void setLastActive(LocalDate lastActive) { this.lastActive = lastActive; }

    public Integer getCoursesCount() { return coursesCount; }
    public void setCoursesCount(Integer coursesCount) { this.coursesCount = coursesCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean isVerified() { return isVerified != null && isVerified; }
    public void setVerified(Boolean verified) { isVerified = verified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
}
