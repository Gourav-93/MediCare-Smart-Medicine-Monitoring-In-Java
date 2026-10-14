package com.backend.medicare.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterDto {

    @NotBlank(message = "Name is required")
    @Builder.Default
    private String name = "";

    @NotBlank(message = "Email is required")
    @Builder.Default
    private String email = "";

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Builder.Default
    private String password = "";

    @NotBlank(message = "Role is required")
    @Builder.Default
    private String role = "";

    // No-Args Constructor
    public RegisterDto() {
    }

    // All-Args Constructor
    public RegisterDto(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}