package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdherenceDto {
    private int patientId;
    private int totalDoses;
    private int takenDoses;
    private int missedDoses;
    private double adherencePercentage;

    // No-Args Constructor
    public AdherenceDto() {
    }

    // All-Args Constructor
    public AdherenceDto(int patientId, int totalDoses, int takenDoses, int missedDoses, double adherencePercentage) {
        this.patientId = patientId;
        this.totalDoses = totalDoses;
        this.takenDoses = takenDoses;
        this.missedDoses = missedDoses;
        this.adherencePercentage = adherencePercentage;
    }
}