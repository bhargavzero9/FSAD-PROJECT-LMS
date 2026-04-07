package com.dbb.controller;

import com.dbb.entity.Message;
import com.dbb.entity.User;
import com.dbb.repository.MessageRepository;
import com.dbb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    public MessageController(MessageRepository messageRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    // ── Helper: map to frontend shape ───────────────────────────────────────
    private Map<String, Object> mapMessage(Message m, User sender, User recipient) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("fromId", m.getSenderId());
        map.put("fromName", m.getSenderName() != null ? m.getSenderName() : (sender != null ? sender.getName() : ""));
        map.put("fromRole", sender != null ? sender.getRole() : "");
        map.put("fromInitials", sender != null ? sender.getInitials() : "");
        map.put("toId", m.getRecipientId());
        map.put("toName", m.getRecipientName() != null ? m.getRecipientName() : (recipient != null ? recipient.getName() : ""));
        map.put("subject", m.getSubject());
        map.put("body", m.getBody());
        map.put("sentAt", m.getSentAt());
        map.put("readByTo", m.getIsRead());
        map.put("deletedBySender", m.getSenderDeleted());
        map.put("deletedByRecipient", m.getRecipientDeleted());
        return map;
    }

    // ── GET /api/messages/inbox/{userId} ────────────────────────────────────
    @GetMapping("/inbox/{userId}")
    public List<Map<String, Object>> getInbox(@PathVariable Integer userId) {
        List<Message> messages = messageRepo.findByRecipientIdAndRecipientDeletedFalseOrderBySentAtDesc(userId);
        return messages.stream().map(m -> {
            User sender = userRepo.findById(m.getSenderId()).orElse(null);
            return mapMessage(m, sender, null);
        }).collect(Collectors.toList());
    }

    // ── GET /api/messages/sent/{userId} ─────────────────────────────────────
    @GetMapping("/sent/{userId}")
    public List<Map<String, Object>> getSent(@PathVariable Integer userId) {
        List<Message> messages = messageRepo.findBySenderIdAndSenderDeletedFalseOrderBySentAtDesc(userId);
        return messages.stream().map(m -> {
            User recipient = userRepo.findById(m.getRecipientId()).orElse(null);
            return mapMessage(m, null, recipient);
        }).collect(Collectors.toList());
    }

    // ── POST /api/messages ──────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body) {
        Object fromIdObj = body.get("fromId");
        Object toIdObj = body.get("toId");
        String msgBody = (String) body.get("body");

        if (fromIdObj == null || toIdObj == null || msgBody == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "fromId, toId, and body are required."));
        }

        Integer fromId = fromIdObj instanceof Number ? ((Number) fromIdObj).intValue() : Integer.parseInt(fromIdObj.toString());
        Integer toId = toIdObj instanceof Number ? ((Number) toIdObj).intValue() : Integer.parseInt(toIdObj.toString());

        User sender = userRepo.findById(fromId).orElse(null);
        User recipient = userRepo.findById(toId).orElse(null);

        String subject = (String) body.getOrDefault("subject", "(No subject)");

        Message msg = new Message();
        msg.setSenderId(fromId);
        msg.setSenderName(sender != null ? sender.getName() : "Unknown");
        msg.setRecipientId(toId);
        msg.setRecipientName(recipient != null ? recipient.getName() : "Unknown");
        msg.setSubject(subject);
        msg.setBody(msgBody);

        Message saved = messageRepo.save(msg);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("fromId", fromId);
        response.put("fromName", sender != null ? sender.getName() : "Unknown");
        response.put("fromRole", sender != null ? sender.getRole() : "");
        response.put("fromInitials", sender != null ? sender.getInitials() : "");
        response.put("toId", toId);
        response.put("toName", recipient != null ? recipient.getName() : "Unknown");
        response.put("subject", subject);
        response.put("body", msgBody);
        response.put("sentAt", saved.getSentAt());
        response.put("readByTo", false);

        return ResponseEntity.status(201).body(response);
    }

    // ── PUT /api/messages/{id}/read ─────────────────────────────────────────
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        messageRepo.findById(id).ifPresent(m -> {
            m.setIsRead(true);
            messageRepo.save(m);
        });
        return ResponseEntity.ok(Map.of("message", "Marked as read."));
    }

    // ── DELETE /api/messages/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id,
                                            @RequestParam(required = false, defaultValue = "false") String asSender) {
        messageRepo.findById(id).ifPresent(m -> {
            if ("true".equals(asSender)) {
                m.setSenderDeleted(true);
            } else {
                m.setRecipientDeleted(true);
            }
            messageRepo.save(m);
        });
        return ResponseEntity.ok(Map.of("message", "Message deleted."));
    }
}
