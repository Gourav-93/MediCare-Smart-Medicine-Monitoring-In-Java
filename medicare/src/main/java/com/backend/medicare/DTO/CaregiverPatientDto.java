package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CaregiverPatientDto {
    private int caregiverId;
    private int patientId;
    private String patientEmail;
    @Builder.Default
    private String relationType = "";

    // No-Args Constructor
    public CaregiverPatientDto() {
    }

    // All-Args Constructor
    public CaregiverPatientDto(int caregiverId, int patientId, String patientEmail, String relationType) {
        this.caregiverId = caregiverId;
        this.patientId = patientId;
        this.patientEmail = patientEmail;
        this.relationType = relationType;
    }
}