package com.dbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender_id", nullable = false)
    @JsonProperty("senderId")
    private Integer senderId;

    @Column(name = "sender_name")
    @JsonProperty("senderName")
    private String senderName = "";

    @Column(name = "recipient_id", nullable = false)
    @JsonProperty("recipientId")
    private Integer recipientId;

    @Column(name = "recipient_name")
    @JsonProperty("recipientName")
    private String recipientName = "";

    private String subject = "";

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "is_read")
    @JsonProperty("isRead")
    private Boolean isRead = false;

    @Column(name = "sender_deleted")
    @JsonProperty("senderDeleted")
    private Boolean senderDeleted = false;

    @Column(name = "recipient_deleted")
    @JsonProperty("recipientDeleted")
    private Boolean recipientDeleted = false;

    @Column(name = "sent_at")
    @JsonProperty("sentAt")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) sentAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Integer getRecipientId() { return recipientId; }
    public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Boolean getSenderDeleted() { return senderDeleted; }
    public void setSenderDeleted(Boolean senderDeleted) { this.senderDeleted = senderDeleted; }

    public Boolean getRecipientDeleted() { return recipientDeleted; }
    public void setRecipientDeleted(Boolean recipientDeleted) { this.recipientDeleted = recipientDeleted; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
