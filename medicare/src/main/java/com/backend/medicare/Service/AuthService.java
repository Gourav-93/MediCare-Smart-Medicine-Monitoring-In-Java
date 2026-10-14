package com.backend.medicare.Service;

import com.backend.medicare.DTO.AuthResponseDto;
import com.backend.medicare.DTO.LoginDto;
import com.backend.medicare.DTO.RegisterDto;
import com.backend.medicare.Model.Caregiver;
import com.backend.medicare.Model.Patient;
import com.backend.medicare.Model.User;
import com.backend.medicare.Repository.CaregiverRepository;
import com.backend.medicare.Repository.PatientRepository;
import com.backend.medicare.Repository.UserRepository;
import com.backend.medicare.Security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PatientRepository patientRepository,
                       CaregiverRepository caregiverRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponseDto register(RegisterDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty() ||
            dto.getEmail() == null || dto.getEmail().trim().isEmpty() ||
            dto.getPassword() == null || dto.getPassword().trim().isEmpty() ||
            dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Name, Email, Password and Role are required.");
        }

        if (dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        String email = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        String role = dto.getRole().trim().toUpperCase();
        if (!"PATIENT".equals(role) && !"CAREGIVER".equals(role)) {
            throw new IllegalArgumentException("Only PATIENT or CAREGIVER registration is allowed.");
        }

        User user = User.builder()
                .name(dto.getName().trim())
                .email(email)
                .role(role)
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();

        user = userRepository.save(user);

        if ("PATIENT".equals(role)) {
            Patient patient = Patient.builder()
                    .user(user)
                    .dateOfBirth(LocalDateTime.now())
                    .emergencyContact("")
                    .build();
            patientRepository.save(patient);
        } else {
            Caregiver caregiver = Caregiver.builder()
                    .user(user)
                    .relationship("")
                    .build();
            caregiverRepository.save(caregiver);
        }

        return AuthResponseDto.builder()
                .message("User registered successfully.")
                .userId(user.getId())
                .role(user.getRole())
                .build();
    }

    public AuthResponseDto login(LoginDto dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() ||
            dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Email and Password are required.");
        }

        String email = dto.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

        return AuthResponseDto.builder()
                .message("Login successful.")
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
