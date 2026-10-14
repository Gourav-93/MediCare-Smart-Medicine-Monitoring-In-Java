package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CaregiverPatients", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"CaregiverId", "PatientId"})
})
@Getter
@Setter
@Builder
public class CaregiverPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "RelationType", nullable = false)
    @Builder.Default
    private String relationType = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CaregiverId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    // No-Args Constructor (Required by JPA/Hibernate)
    public CaregiverPatient() {
    }

    // All-Args Constructor
    public CaregiverPatient(Integer id, String relationType, Caregiver caregiver, Patient patient) {
        this.id = id;
        this.relationType = relationType;
        this.caregiver = caregiver;
        this.patient = patient;
    }

    public Integer getCaregiverId() {
        return caregiver != null ? caregiver.getId() : null;
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}