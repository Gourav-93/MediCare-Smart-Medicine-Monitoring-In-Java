package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CaregiverPatients", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"CaregiverId", "PatientId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaregiverPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "RelationType", nullable = false)
    private String relationType = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CaregiverId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    public Integer getCaregiverId() {
        return caregiver != null ? caregiver.getId() : null;
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}