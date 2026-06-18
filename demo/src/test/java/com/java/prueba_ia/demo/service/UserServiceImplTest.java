package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.dto.user.UserUpdateRequest;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
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
    private User otherUser;
    private Collection<GrantedAuthority> userAuthorities;
    private Collection<GrantedAuthority> adminAuthorities;

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

        otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .password("encoded")
                .email("other@example.com")
                .fullName("Other User")
                .role(Role.USER)
                .build();

        userAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
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
    void findById_OwnUser_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(1L, "testuser", userAuthorities);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findById_AsAdmin_ShouldReturnAnyUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        UserResponse result = userService.findById(2L, "admin", adminAuthorities);

        assertNotNull(result);
        assertEquals("otheruser", result.getUsername());
    }

    @Test
    void findById_OtherUser_NotAdmin_ShouldThrow() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        assertThrows(IllegalArgumentException.class,
                () -> userService.findById(2L, "testuser", userAuthorities));
    }

    @Test
    void findById_NotFound_ShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findById(99L, "testuser", userAuthorities));
    }

    @Test
    void update_OwnUser_ShouldSucceed() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Updated Name");
        request.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.update(1L, request, "testuser", userAuthorities);

        assertNotNull(result);
        assertEquals("Updated Name", user.getFullName());
        assertEquals("updated@example.com", user.getEmail());
    }

    @Test
    void update_OtherUser_NotAdmin_ShouldThrow() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Hacked Name");
        request.setEmail("hacked@example.com");

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        assertThrows(IllegalArgumentException.class,
                () -> userService.update(2L, request, "testuser", userAuthorities));
    }

    @Test
    void update_DuplicateEmail_ShouldThrow() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Test User");
        request.setEmail("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.update(1L, request, "testuser", userAuthorities));
    }

    @Test
    void update_NotFound_ShouldThrow() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Ghost");
        request.setEmail("ghost@example.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(99L, request, "testuser", userAuthorities));
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
