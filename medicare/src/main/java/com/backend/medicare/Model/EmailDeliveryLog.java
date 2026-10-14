package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmailDeliveryLogs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"MedicineScheduleId", "ScheduledOccurrence", "NotificationType", "RecipientEmail"})
})
@Getter
@Setter
@Builder
public class EmailDeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ScheduledOccurrence", nullable = false)
    private LocalDateTime scheduledOccurrence;

    @Column(name = "NotificationType", nullable = false)
    @Builder.Default
    private String notificationType = "";

    @Column(name = "RecipientEmail", nullable = false)
    @Builder.Default
    private String recipientEmail = "";

    @Column(name = "Status", nullable = false)
    @Builder.Default
    private String status = "";

    @Column(name = "SentAt")
    private LocalDateTime sentAt;

    @Column(name = "RetryCount", nullable = false)
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "ErrorMessage", columnDefinition = "LONGTEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MedicineScheduleId", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MedicineSchedule medicineSchedule;

    // No-Args Constructor (Required by JPA/Hibernate)
    public EmailDeliveryLog() {
    }

    // All-Args Constructor
    public EmailDeliveryLog(
            Integer id,
            LocalDateTime scheduledOccurrence,
            String notificationType,
            String recipientEmail,
            String status,
            LocalDateTime sentAt,
            int retryCount,
            String errorMessage,
            MedicineSchedule medicineSchedule
    ) {
        this.id = id;
        this.scheduledOccurrence = scheduledOccurrence;
        this.notificationType = notificationType;
        this.recipientEmail = recipientEmail;
        this.status = status;
        this.sentAt = sentAt;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.medicineSchedule = medicineSchedule;
    }

    public Integer getMedicineScheduleId() {
        return medicineSchedule != null ? medicineSchedule.getId() : null;
    }
}