package com.backend.medicare.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAdherenceDto {
    private int patientId;
    private String patientName = "";
    private int totalDoses;
    private int takenDoses;
    private int missedDoses;
    private double adherencePercentage;
}
