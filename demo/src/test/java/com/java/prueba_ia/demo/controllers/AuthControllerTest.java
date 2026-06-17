package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.auth.AuthResponse;
import com.java.prueba_ia.demo.dto.auth.LoginRequest;
import com.java.prueba_ia.demo.dto.auth.RegisterRequest;
import com.java.prueba_ia.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void register_ShouldReturn201() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setFullName("Test User");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .token("jwt-token")
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("USER")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals("jwt-token", result.getBody().getToken());
        assertEquals("Test User", result.getBody().getFullName());
    }

    @Test
    void login_ShouldReturn200() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .token("jwt-token")
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("USER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("jwt-token", result.getBody().getToken());
    }

    @Test
    void me_ShouldReturn200() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("USER")
                .build();

        when(authService.me("testuser")).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.me(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
        assertEquals("Test User", result.getBody().getFullName());
    }
}
