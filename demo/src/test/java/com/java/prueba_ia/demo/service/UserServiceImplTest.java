package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Role;
import com.java.prueba_ia.demo.entity.User;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.repository.LoanRepository;
import com.java.prueba_ia.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded")
                .email("test@example.com")
                .fullName("Test User")
                .role(Role.USER)
                .build();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("testuser", result.getFirst().getUsername());
        assertEquals("test@example.com", result.getFirst().getEmail());
        assertEquals("Test User", result.getFirst().getFullName());
        assertEquals("USER", result.getFirst().getRole());
    }

    @Test
    void delete_ShouldSucceed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(false);

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void delete_WithActiveLoans_ShouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.delete(1L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void delete_WithOnlyReturnedLoans_ShouldSucceed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(false);

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void delete_NotFound_ShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).delete(any());
    }
}
