package com.java.prueba_ia.demo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "mySuperSecretKeyForJwtThatIsLongEnoughToBeValidHS256Key1234567890";
    private static final long EXPIRATION = 3600000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateToken("testuser", "USER");

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("testuser", "USER");

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        String role = jwtUtil.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateToken("testuser", "USER");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        JwtUtil shortLived = new JwtUtil(SECRET, -1000);
        String token = shortLived.generateToken("testuser", "USER");

        assertFalse(shortLived.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithTamperedToken_ShouldReturnFalse() {
        String token = jwtUtil.generateToken("testuser", "USER") + "tampered";

        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithInvalidString_ShouldReturnFalse() {
        assertFalse(jwtUtil.isTokenValid("invalid-token-string"));
    }
}
