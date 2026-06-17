package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.User;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.repository.LoanRepository;
import com.java.prueba_ia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (loanRepository.existsByUserIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new IllegalStateException("No se puede eliminar el usuario porque tiene préstamos activos");
        }

        userRepository.delete(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
