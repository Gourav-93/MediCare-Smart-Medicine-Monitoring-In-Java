package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientAdherenceDto {
    private int patientId;
    @Builder.Default
    private String patientName = "";
    private int totalDoses;
    private int takenDoses;
    private int missedDoses;
    private double adherencePercentage;

    // No-Args Constructor
    public PatientAdherenceDto() {
    }

    // All-Args Constructor
    public PatientAdherenceDto(int patientId, String patientName, int totalDoses, int takenDoses, int missedDoses, double adherencePercentage) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.totalDoses = totalDoses;
        this.takenDoses = takenDoses;
        this.missedDoses = missedDoses;
        this.adherencePercentage = adherencePercentage;
    }
}