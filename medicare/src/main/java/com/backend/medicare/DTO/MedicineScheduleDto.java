package com.backend.medicare.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineScheduleDto {
    private int medicineId;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime time;

    private String frequency = "";
}
