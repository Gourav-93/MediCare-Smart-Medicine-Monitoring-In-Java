package com.backend.medicare.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaregiverDashboardPatientDto {
    private int patientId;
    private int userId;
    private String name = "";
    private String email = "";
    private LocalDateTime dateOfBirth;
    private String emergencyContact = "";
}
