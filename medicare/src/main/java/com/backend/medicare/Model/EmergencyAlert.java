package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmergencyAlerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Message", nullable = false)
    private String message = "";

    @Column(name = "Status", nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ResolvedAt")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}