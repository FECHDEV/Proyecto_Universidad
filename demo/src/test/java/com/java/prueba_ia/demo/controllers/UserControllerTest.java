package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void findAll_ShouldReturn200() {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("USER")
                .build();

        when(userService.findAll()).thenReturn(List.of(response));

        ResponseEntity<List<UserResponse>> result = userController.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("testuser", result.getBody().getFirst().getUsername());
    }

    @Test
    void delete_ShouldReturn204() {
        ResponseEntity<Void> result = userController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).delete(1L);
    }
}
