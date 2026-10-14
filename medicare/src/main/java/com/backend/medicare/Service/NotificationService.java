package com.backend.medicare.Service;

import com.backend.medicare.Model.Notification;
import com.backend.medicare.Repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllForUser(int currentUserId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return notificationRepository.findAllByOrderByCreatedAtDesc();
        }
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(currentUserId);
    }

    public Optional<Notification> getById(int id) {
        return notificationRepository.findById(id);
    }

    @Transactional
    public Notification create(Notification notification) {
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification markAsRead(int id, int currentUserId, String role) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));

        if (!"ADMIN".equalsIgnoreCase(role) && !notification.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Unauthorized to access this notification.");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(int currentUserId) {
        notificationRepository.markAllAsReadByUserId(currentUserId);
    }
}
