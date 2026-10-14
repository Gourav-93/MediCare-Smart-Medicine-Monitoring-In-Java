package com.backend.medicare.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineLogDto {
    private int medicineId;
    private int patientId;
    private LocalDateTime scheduledTime;
    private String status = "";
}
