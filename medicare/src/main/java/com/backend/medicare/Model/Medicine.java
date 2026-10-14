package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Medicines")
@Getter
@Setter
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
    @JsonIgnoreProperties({ "caregiverPatients", "hibernateLazyInitializer", "handler" })
    private Patient patient;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "medicine", "hibernateLazyInitializer", "handler" })
    private List<MedicineSchedule> medicineSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "medicine", "hibernateLazyInitializer", "handler" })
    private List<MedicineLog> medicineLogs = new ArrayList<>();

    // 1. Default No-Args Constructor (JPA requirement)
    public Medicine() {
    }

    // 2. All-Args Constructor
    @Builder
    public Medicine(Integer id, String name, String dosage, int dose, int stock,
            LocalDateTime startDate, LocalDateTime endDate, Patient patient,
            List<MedicineSchedule> medicineSchedules, List<MedicineLog> medicineLogs) {
        this.id = id;
        this.name = (name != null) ? name : "";
        this.dosage = (dosage != null) ? dosage : "";
        this.dose = dose;
        this.stock = stock;
        this.startDate = startDate;
        this.endDate = endDate;
        this.patient = patient;
        this.medicineSchedules = (medicineSchedules != null) ? medicineSchedules : new ArrayList<>();
        this.medicineLogs = (medicineLogs != null) ? medicineLogs : new ArrayList<>();
    }

    // 3. Convenience Constructor (New Medicine create karte waqt useful)
    public Medicine(String name, String dosage, int dose, int stock,
            LocalDateTime startDate, LocalDateTime endDate, Patient patient) {
        this.name = name;
        this.dosage = dosage;
        this.dose = dose;
        this.stock = stock;
        this.startDate = startDate;
        this.endDate = endDate;
        this.patient = patient;
        this.medicineSchedules = new ArrayList<>();
        this.medicineLogs = new ArrayList<>();
    }

    public Integer getPatientId() {
        return patient != null ? patient.getId() : null;
    }
}