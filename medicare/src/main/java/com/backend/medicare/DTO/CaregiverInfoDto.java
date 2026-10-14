package com.backend.medicare.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CaregiverInfoDto {
    private int id;
    private int userId;
    @Builder.Default
    private String name = "";
    @Builder.Default
    private String email = "";
    @Builder.Default
    private String relationship = "";

    // No-Args Constructor
    public CaregiverInfoDto() {
    }

    // All-Args Constructor
    public CaregiverInfoDto(int id, int userId, String name, String email, String relationship) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.relationship = relationship;
    }
}