package com.project.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userservice.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    
    Optional<List<Notification>> findByIsRead(boolean isRead);
}
