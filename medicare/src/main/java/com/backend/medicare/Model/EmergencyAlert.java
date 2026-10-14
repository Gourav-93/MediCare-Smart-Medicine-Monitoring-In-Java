package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmergencyAlerts")
@Getter
@Setter
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Message", nullable = false)
    private String message = "";

    @Column(name = "Status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ResolvedAt")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({ "caregiverPatients", "hibernateLazyInitializer", "handler" })
    private Patient patient;

    // 1. Default No-Args Constructor (JPA requirement)
    public EmergencyAlert() {
    }

    // 2. All-Args Constructor (Saari fields set karne ke liye)
    @Builder
    public EmergencyAlert(Integer id, String message, String status, LocalDateTime createdAt, LocalDateTime resolvedAt,
            Patient patient) {
        this.id = id;
        this.message = (message != null) ? message : "";
        this.status = (status != null) ? status : "ACTIVE";
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.patient = patient;
    }

    // 3. Convenient Constructor (New Alert create karne ke liye - ID aur timestamps
    // auto-handle)
    public EmergencyAlert(String message, Patient patient) {
        this.message = message;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.patient = patient;
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}