package com.dbb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "platform_settings")
public class PlatformSettings {

    @Id
    private Integer id = 1;

    @Column(name = "platform_name")
    private String platformName = "Digital Black Board";

    @Column(name = "contact_email")
    private String contactEmail = "digitalblackboardlms@gmail.com";

    @Column(name = "contact_phone")
    private String contactPhone = "9100260825";

    @Column(name = "about_text", columnDefinition = "TEXT")
    private String aboutText = "Welcome to Digital Black Board, the premier platform for modern learning management!";

    @Column(name = "max_students")
    private Integer maxStudents = 500;

    @Column(name = "public_courses")
    private Boolean publicCourses = true;

    @Column(name = "guest_enroll")
    private Boolean guestEnroll = false;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;

    @Column(name = "auto_grade")
    private Boolean autoGrade = true;

    @Column(name = "certificates_enabled")
    private Boolean certificatesEnabled = true;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAboutText() { return aboutText; }
    public void setAboutText(String aboutText) { this.aboutText = aboutText; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Boolean getPublicCourses() { return publicCourses != null && publicCourses; }
    public void setPublicCourses(Boolean publicCourses) { this.publicCourses = publicCourses; }

    public Boolean getGuestEnroll() { return guestEnroll != null && guestEnroll; }
    public void setGuestEnroll(Boolean guestEnroll) { this.guestEnroll = guestEnroll; }

    public Boolean getMaintenanceMode() { return maintenanceMode != null && maintenanceMode; }
    public void setMaintenanceMode(Boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public Boolean getAutoGrade() { return autoGrade != null && autoGrade; }
    public void setAutoGrade(Boolean autoGrade) { this.autoGrade = autoGrade; }

    public Boolean getCertificatesEnabled() { return certificatesEnabled != null && certificatesEnabled; }
    public void setCertificatesEnabled(Boolean certificatesEnabled) { this.certificatesEnabled = certificatesEnabled; }
}
