package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "MedicineSchedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    // EF Core: time(6) -> Java: LocalTime
    @Column(name = "Time", nullable = false)
    private LocalTime time;

    @Column(name = "Frequency", nullable = false)
    private String frequency = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MedicineId", nullable = false)
    @JsonIgnoreProperties({"medicineSchedules", "medicineLogs", "hibernateLazyInitializer", "handler"})
    private Medicine medicine;

    public Integer getMedicineId() {
        return medicine != null ? medicine.getId() : null;
    }
}