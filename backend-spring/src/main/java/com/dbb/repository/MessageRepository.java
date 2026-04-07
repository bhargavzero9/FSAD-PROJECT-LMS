package com.dbb.repository;

import com.dbb.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByRecipientIdAndRecipientDeletedFalseOrderBySentAtDesc(Integer recipientId);
    List<Message> findBySenderIdAndSenderDeletedFalseOrderBySentAtDesc(Integer senderId);
}
