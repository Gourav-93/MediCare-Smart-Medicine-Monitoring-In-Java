package com.backend.medicare.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaregiverInfoDto {
    private int id;
    private int userId;
    private String name = "";
    private String email = "";
    private String relationship = "";
}
