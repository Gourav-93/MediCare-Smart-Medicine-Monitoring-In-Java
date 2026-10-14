package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MedicineDto {
    private int patientId;
    @Builder.Default
    private String name = "";
    @Builder.Default
    private String dosage = "";
    private int dose;
    private int stock;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // No-Args Constructor
    public MedicineDto() {
    }

    // All-Args Constructor
    public MedicineDto(int patientId, String name, String dosage, int dose, int stock, LocalDateTime startDate, LocalDateTime endDate) {
        this.patientId = patientId;
        this.name = name;
        this.dosage = dosage;
        this.dose = dose;
        this.stock = stock;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}