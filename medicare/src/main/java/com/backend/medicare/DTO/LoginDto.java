package com.backend.medicare.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginDto {

    @NotBlank(message = "Email is required")
    @Builder.Default
    private String email = "";

    @NotBlank(message = "Password is required")
    @Builder.Default
    private String password = "";

    // No-Args Constructor
    public LoginDto() {
    }

    // All-Args Constructor
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}