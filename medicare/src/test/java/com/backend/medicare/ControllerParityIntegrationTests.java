package com.backend.medicare;

import com.backend.medicare.Model.User;
import com.backend.medicare.Repository.UserRepository;
import com.backend.medicare.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerParityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String caregiverToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@medicare.com")
                .passwordHash("DummyHash")
                .role("ADMIN")
                .build());

        User caregiver = userRepository.save(User.builder()
                .name("Caregiver User")
                .email("caregiver@medicare.com")
                .passwordHash("DummyHash")
                .role("CAREGIVER")
                .build());

        adminToken = "Bearer " + jwtTokenProvider.generateToken(admin.getId(), admin.getEmail(), admin.getRole());
        caregiverToken = "Bearer " + jwtTokenProvider.generateToken(caregiver.getId(), caregiver.getEmail(), caregiver.getRole());
    }

    @Test
    void testAuthRegister_ValidationMissingFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"\",\"password\":\"\",\"role\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name, Email, Password and Role are required."));
    }

    @Test
    void testAuthRegister_ShortPassword_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"test@test.com\",\"password\":\"123\",\"role\":\"PATIENT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 6 characters long."));
    }

    @Test
    void testAuthLogin_MissingCredentials_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email and Password are required."));
    }

    @Test
    void testAuthLogin_InvalidUser_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@medicare.com\",\"password\":\"ValidPass123!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    void testUnauthenticatedAccess_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/medicine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testResolveNonExistentEmergency_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/emergency/99999/resolve")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Emergency alert not found."));
    }

    @Test
    void testMarkAsTaken_WithCaregiverRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/medicinelog/1/taken")
                        .header(HttpHeaders.AUTHORIZATION, caregiverToken))
                .andExpect(status().isForbidden());
    }
}
