package com.backend.medicare.Security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Custom PasswordEncoder fully compatible with ASP.NET Core Identity Version 3 PBKDF2-HMAC-SHA256.
 * Ensures passwords hashed in .NET can be verified in Java, and passwords hashed in Java can be verified in .NET.
 */
@Component
public class AspNetIdentityPasswordEncoder implements PasswordEncoder {

    private static final byte FORMAT_MARKER = 0x01; // Version 3
    private static final int PRF_HMAC_SHA256 = 1;
    private static final int DEFAULT_ITERATION_COUNT = 10000;
    private static final int SALT_SIZE = 16; // 128-bit
    private static final int SUBKEY_SIZE = 32; // 256-bit

    private final BCryptPasswordEncoder bCryptFallback = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        byte[] salt = new byte[SALT_SIZE];
        secureRandom.nextBytes(salt);

        byte[] subkey = pbkdf2(rawPassword.toString().toCharArray(), salt, DEFAULT_ITERATION_COUNT, SUBKEY_SIZE * 8, "PBKDF2WithHmacSHA256");

        // Format: { 0x01, prf (4 bytes big-endian), iter count (4 bytes big-endian), salt length (4 bytes big-endian), salt, subkey }
        int payloadLength = 1 + 4 + 4 + 4 + salt.length + subkey.length;
        ByteBuffer buffer = ByteBuffer.allocate(payloadLength).order(ByteOrder.BIG_ENDIAN);
        buffer.put(FORMAT_MARKER);
        buffer.putInt(PRF_HMAC_SHA256);
        buffer.putInt(DEFAULT_ITERATION_COUNT);
        buffer.putInt(salt.length);
        buffer.put(salt);
        buffer.put(subkey);

        return Base64.getEncoder().encodeToString(buffer.array());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        // Check if BCrypt format
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bCryptFallback.matches(rawPassword, encodedPassword);
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encodedPassword);
            if (decoded.length < 13) {
                return false;
            }

            // ASP.NET Identity V3 Format check
            if (decoded[0] == FORMAT_MARKER) {
                ByteBuffer buffer = ByteBuffer.wrap(decoded).order(ByteOrder.BIG_ENDIAN);
                buffer.get(); // skip format marker
                int prf = buffer.getInt();
                int iterCount = buffer.getInt();
                int saltLength = buffer.getInt();

                if (saltLength <= 0 || saltLength > decoded.length - 13) {
                    return false;
                }

                byte[] salt = new byte[saltLength];
                buffer.get(salt);

                int subkeyLength = decoded.length - 13 - saltLength;
                byte[] expectedSubkey = new byte[subkeyLength];
                buffer.get(expectedSubkey);

                String algorithm;
                switch (prf) {
                    case 0:
                        algorithm = "PBKDF2WithHmacSHA1";
                        break;
                    case 1:
                        algorithm = "PBKDF2WithHmacSHA256";
                        break;
                    case 2:
                        algorithm = "PBKDF2WithHmacSHA512";
                        break;
                    default:
                        return false;
                }

                byte[] actualSubkey = pbkdf2(rawPassword.toString().toCharArray(), salt, iterCount, subkeyLength * 8, algorithm);
                return MessageDigest.isEqual(actualSubkey, expectedSubkey);
            }

            // ASP.NET Identity V2 Format: { 0x00, salt (16 bytes), subkey (32 bytes) }
            if (decoded[0] == 0x00 && decoded.length == 49) {
                byte[] salt = new byte[16];
                System.arraycopy(decoded, 1, salt, 0, 16);
                byte[] expectedSubkey = new byte[32];
                System.arraycopy(decoded, 17, expectedSubkey, 0, 32);

                byte[] actualSubkey = pbkdf2(rawPassword.toString().toCharArray(), salt, 1000, 256, "PBKDF2WithHmacSHA1");
                return MessageDigest.isEqual(actualSubkey, expectedSubkey);
            }
        } catch (Exception e) {
            // Fallback attempt with BCrypt in case of encoding anomaly
            try {
                return bCryptFallback.matches(rawPassword, encodedPassword);
            } catch (Exception ignored) {
                return false;
            }
        }

        return false;
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits, String algorithm) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 algorithm error: " + algorithm, e);
        }
    }
}
