package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmailDeliveryLogs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"MedicineScheduleId", "ScheduledOccurrence", "NotificationType", "RecipientEmail"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ScheduledOccurrence", nullable = false)
    private LocalDateTime scheduledOccurrence;

    @Column(name = "NotificationType", nullable = false)
    private String notificationType = "";

    @Column(name = "RecipientEmail", nullable = false)
    private String recipientEmail = "";

    @Column(name = "Status", nullable = false)
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

    public Integer getMedicineScheduleId() {
        return medicineSchedule != null ? medicineSchedule.getId() : null;
    }
}