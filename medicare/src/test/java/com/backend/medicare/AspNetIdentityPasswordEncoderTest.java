package com.backend.medicare;

import com.backend.medicare.Security.AspNetIdentityPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AspNetIdentityPasswordEncoderTest {

    private AspNetIdentityPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new AspNetIdentityPasswordEncoder();
    }

    @Test
    void testEncodeAndMatch() {
        String rawPassword = "Password@123";
        String hash = encoder.encode(rawPassword);

        assertNotNull(hash);
        assertTrue(encoder.matches(rawPassword, hash));
        assertFalse(encoder.matches("WrongPassword", hash));
    }

    @Test
    void testEmptyOrNullPasswords() {
        assertFalse(encoder.matches(null, "someHash"));
        assertFalse(encoder.matches("somePassword", null));
        assertFalse(encoder.matches("somePassword", ""));
    }
}
