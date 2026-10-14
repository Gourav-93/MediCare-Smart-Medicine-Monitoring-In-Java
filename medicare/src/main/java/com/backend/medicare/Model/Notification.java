package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Getter
@Setter
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Title", nullable = false)
    @Builder.Default
    private String title = "";

    @Column(name = "Message", nullable = false)
    @Builder.Default
    private String message = "";

    @Column(name = "Type", nullable = false)
    @Builder.Default
    private String type = "";

    @Column(name = "IsRead", nullable = false)
    private boolean isRead;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    @JsonIgnoreProperties({"patient", "caregiver", "hibernateLazyInitializer", "handler"})
    private User user;

    // No-argument constructor
    public Notification() {
    }

    // All-argument constructor
    public Notification(Integer id, String title, String message,
                        String type, boolean isRead,
                        LocalDateTime createdAt, User user) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}