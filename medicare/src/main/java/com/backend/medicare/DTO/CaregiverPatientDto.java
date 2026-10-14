package com.backend.medicare.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaregiverPatientDto {
    private int caregiverId;
    private int patientId;
    private String patientEmail;
    private String relationType = "";
}
