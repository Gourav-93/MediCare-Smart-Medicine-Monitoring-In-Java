package com.backend.medicare.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDto {
    private int patientId;
    private String name = "";
    private String dosage = "";
    private int dose;
    private int stock;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
