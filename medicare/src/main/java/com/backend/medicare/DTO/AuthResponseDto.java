package com.backend.medicare.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {
    private String message;
    private String token;
    private Integer userId;
    private String name;
    private String email;
    private String role;

    // No-Args Constructor
    public AuthResponseDto() {
    }

    // All-Args Constructor
    public AuthResponseDto(String message, String token, Integer userId, String name, String email, String role) {
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}