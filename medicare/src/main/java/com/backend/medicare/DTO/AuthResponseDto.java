package com.backend.medicare.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {
    private String message;
    private String token;
    private Integer userId;
    private String name;
    private String email;
    private String role;
}
