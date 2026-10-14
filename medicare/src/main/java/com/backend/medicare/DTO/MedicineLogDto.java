package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MedicineLogDto {
    private int medicineId;
    private int patientId;
    private LocalDateTime scheduledTime;
    @Builder.Default
    private String status = "";

    // No-Args Constructor
    public MedicineLogDto() {
    }

    // All-Args Constructor
    public MedicineLogDto(int medicineId, int patientId, LocalDateTime scheduledTime, String status) {
        this.medicineId = medicineId;
        this.patientId = patientId;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }
}