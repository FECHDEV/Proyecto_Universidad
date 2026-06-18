package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.dto.user.UserUpdateRequest;
import com.java.prueba_ia.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private UserController userController;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);

        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("USER")
                .build();

        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void findAll_ShouldReturn200() {
        when(userService.findAll()).thenReturn(List.of(userResponse));

        ResponseEntity<List<UserResponse>> result = userController.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("testuser", result.getBody().getFirst().getUsername());
    }

    @Test
    void findById_ShouldReturnUser() {
        when(userService.findById(anyLong(), anyString(), anyCollection())).thenReturn(userResponse);

        ResponseEntity<UserResponse> result = userController.findById(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testuser", result.getBody().getUsername());
    }

    @Test
    void update_ShouldReturnUpdatedUser() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Updated Name");
        request.setEmail("updated@example.com");

        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("updated@example.com")
                .fullName("Updated Name")
                .role("USER")
                .build();

        when(userService.update(anyLong(), any(UserUpdateRequest.class), anyString(), anyCollection()))
                .thenReturn(updatedResponse);

        ResponseEntity<UserResponse> result = userController.update(1L, request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Updated Name", result.getBody().getFullName());
        assertEquals("updated@example.com", result.getBody().getEmail());
    }

    @Test
    void delete_ShouldReturn204() {
        ResponseEntity<Void> result = userController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).delete(1L);
    }
}
