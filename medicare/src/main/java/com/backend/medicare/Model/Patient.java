package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Patients")
@Getter
@Setter
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "DateOfBirth", nullable = false)
    private LocalDateTime dateOfBirth;

    @Column(name = "EmergencyContact", nullable = false)
    @Builder.Default
    private String emergencyContact = "";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    @JsonIgnoreProperties({"patient", "caregiver", "hibernateLazyInitializer", "handler"})
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"patient", "hibernateLazyInitializer", "handler"})
    private List<CaregiverPatient> caregiverPatients = new ArrayList<>();

    // No-argument constructor
    public Patient() {
    }

    // All-argument constructor
    public Patient(Integer id, LocalDateTime dateOfBirth,
                   String emergencyContact, User user,
                   List<CaregiverPatient> caregiverPatients) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.user = user;
        this.caregiverPatients = caregiverPatients;
    }

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}