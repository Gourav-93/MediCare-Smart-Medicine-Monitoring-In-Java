package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PatientInfoDto {
    private int id;
    private int userId;
    @Builder.Default
    private String name = "";
    @Builder.Default
    private String email = "";
    private LocalDateTime dateOfBirth;
    @Builder.Default
    private String emergencyContact = "";

    // No-Args Constructor
    public PatientInfoDto() {
    }

    // All-Args Constructor
    public PatientInfoDto(int id, int userId, String name, String email, LocalDateTime dateOfBirth, String emergencyContact) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
    }
}