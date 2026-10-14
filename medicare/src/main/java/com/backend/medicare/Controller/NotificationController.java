package com.backend.medicare.Controller;

import com.backend.medicare.Model.Notification;
import com.backend.medicare.Repository.UserRepository;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/notification", "/api/Notification"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Notification> notifications = notificationService.getAllForUser(userPrincipal.getId(), userPrincipal.getRole());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Notification> notificationOpt = notificationService.getById(id);
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found.");
        }

        Notification notification = notificationOpt.get();
        if (!"ADMIN".equalsIgnoreCase(userPrincipal.getRole()) && !notification.getUser().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        return ResponseEntity.ok(notification);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody Notification notification) {
        if (notification.getUser() == null || notification.getUser().getId() == null ||
            !userRepository.existsById(notification.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Notification created = notificationService.create(notification);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Notification result = notificationService.markAsRead(id, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(result);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok("All notifications marked as read.");
    }
}
