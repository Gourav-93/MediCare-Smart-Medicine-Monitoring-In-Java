package com.backend.medicare.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientInfoDto {
    private int id;
    private int userId;
    private String name = "";
    private String email = "";
    private LocalDateTime dateOfBirth;
    private String emergencyContact = "";
}
