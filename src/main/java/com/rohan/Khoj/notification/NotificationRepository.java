package com.rohan.Khoj.notification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
