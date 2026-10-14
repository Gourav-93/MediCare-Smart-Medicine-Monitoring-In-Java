package com.backend.medicare;

import com.backend.medicare.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "MediCare_SuperSecret_Key_2026_Gourav@1234567890@@112@");
        ReflectionTestUtils.setField(tokenProvider, "jwtIssuer", "MediCareAPI");
        ReflectionTestUtils.setField(tokenProvider, "jwtAudience", "MediCareClient");
        ReflectionTestUtils.setField(tokenProvider, "expiryMinutes", 60);
        tokenProvider.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = tokenProvider.generateToken(42, "patient@medicare.com", "PATIENT");

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(42, tokenProvider.getUserIdFromToken(token));
        assertEquals("patient@medicare.com", tokenProvider.getEmailFromToken(token));
        assertEquals("PATIENT", tokenProvider.getRoleFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.string"));
    }
}
