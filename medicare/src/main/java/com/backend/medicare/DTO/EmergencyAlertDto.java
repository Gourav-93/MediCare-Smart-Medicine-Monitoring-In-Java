package com.backend.medicare.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlertDto {
    private String message = "";
}
