package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.config.JwtUtil;
import com.java.prueba_ia.demo.dto.auth.AuthResponse;
import com.java.prueba_ia.demo.dto.auth.LoginRequest;
import com.java.prueba_ia.demo.dto.auth.RegisterRequest;
import com.java.prueba_ia.demo.entity.Role;
import com.java.prueba_ia.demo.entity.User;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setFullName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .fullName("Test User")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_ShouldSucceed() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getId() == null) u.setId(1L);
            return u;
        });

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("USER", response.getRole());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("testuser", captor.getValue().getUsername());
        assertEquals("encodedPassword", captor.getValue().getPassword());
        assertEquals(Role.USER, captor.getValue().getRole());
    }

    @Test
    void register_WithAdminRole_ShouldSucceed() {
        registerRequest.setRole("ADMIN");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getId() == null) u.setId(1L);
            return u;
        });

        AuthResponse response = authService.register(registerRequest);

        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void register_WithInvalidRole_ShouldThrow() {
        registerRequest.setRole("INVALID");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void register_DuplicateUsername_ShouldThrow() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void register_DuplicateEmail_ShouldThrow() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_ShouldSucceed() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("USER", response.getRole());
    }

    @Test
    void login_WrongUsername_ShouldThrow() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_WrongPassword_ShouldThrow() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
    }

    @Test
    void me_ShouldReturnUserData() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AuthResponse response = authService.me("testuser");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("USER", response.getRole());
    }

    @Test
    void me_NotFound_ShouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.me("unknown"));
    }
}
