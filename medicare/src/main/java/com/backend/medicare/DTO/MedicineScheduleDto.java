package com.backend.medicare.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class MedicineScheduleDto {
    private int medicineId;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime time;

    @Builder.Default
    private String frequency = "";

    // No-Args Constructor
    public MedicineScheduleDto() {
    }

    // All-Args Constructor
    public MedicineScheduleDto(int medicineId, LocalTime time, String frequency) {
        this.medicineId = medicineId;
        this.time = time;
        this.frequency = frequency;
    }
}