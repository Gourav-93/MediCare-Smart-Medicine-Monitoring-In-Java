package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MedicineLogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ScheduledTime", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "TakenTime")
    private LocalDateTime takenTime;

    @Column(name = "Status", nullable = false)
    private String status = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MedicineId", nullable = false)
    @JsonIgnoreProperties({"medicineLogs", "medicineSchedules", "hibernateLazyInitializer", "handler"})
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    public Integer getMedicineId() {
        return medicine != null ? medicine.getId() : null;
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}