package com.backend.medicare.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdherenceDto {
    private int patientId;
    private int totalDoses;
    private int takenDoses;
    private int missedDoses;
    private double adherencePercentage;
}
