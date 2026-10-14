package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CaregiverDashboardPatientDto {
    private int patientId;
    private int userId;
    @Builder.Default
    private String name = "";
    @Builder.Default
    private String email = "";
    private LocalDateTime dateOfBirth;
    @Builder.Default
    private String emergencyContact = "";

    // No-Args Constructor
    public CaregiverDashboardPatientDto() {
    }

    // All-Args Constructor
    public CaregiverDashboardPatientDto(int patientId, int userId, String name, String email, LocalDateTime dateOfBirth, String emergencyContact) {
        this.patientId = patientId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
    }
}