package com.backend.medicare.Controller;

import com.backend.medicare.DTO.AuthResponseDto;
import com.backend.medicare.DTO.LoginDto;
import com.backend.medicare.DTO.RegisterDto;
import com.backend.medicare.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/Auth", "/api/auth"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        try {
            AuthResponseDto response = authService.register(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            AuthResponseDto response = authService.login(dto);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
