package com.notification.notification_service.Repository;

import com.notification.notification_service.Entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Notification_Repository extends JpaRepository<NotificationHistory,Long> {
    NotificationHistory findByUserId(Long id);

    Optional<NotificationHistory> findById(Long id);

    List<NotificationHistory> findAll();
}
