package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.dto.user.UserUpdateRequest;
import com.java.prueba_ia.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.findById(id, authentication.getName(), authentication.getAuthorities()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UserUpdateRequest request,
                                                Authentication authentication) {
        return ResponseEntity.ok(userService.update(id, request, authentication.getName(), authentication.getAuthorities()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
