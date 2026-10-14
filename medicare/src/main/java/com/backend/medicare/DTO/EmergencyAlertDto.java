package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmergencyAlertDto {
    @Builder.Default
    private String message = "";

    // No-Args Constructor
    public EmergencyAlertDto() {
    }

    // All-Args Constructor
    public EmergencyAlertDto(String message) {
        this.message = message;
    }
}