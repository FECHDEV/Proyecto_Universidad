package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void delete_ShouldReturn204() {
        ResponseEntity<Void> result = userController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).delete(1L);
    }
}
