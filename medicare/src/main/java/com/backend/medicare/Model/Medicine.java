package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name = "";

    @Column(name = "Dosage", nullable = false)
    private String dosage = "";

    @Column(name = "Dose", nullable = false)
    private int dose;

    @Column(name = "Stock", nullable = false)
    private int stock;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    @JsonIgnoreProperties({"caregiverPatients", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"medicine", "hibernateLazyInitializer", "handler"})
    private List<MedicineSchedule> medicineSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"medicine", "hibernateLazyInitializer", "handler"})
    private List<MedicineLog> medicineLogs = new ArrayList<>();

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}