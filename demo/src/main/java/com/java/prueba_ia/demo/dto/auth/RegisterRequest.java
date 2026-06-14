package com.java.prueba_ia.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 5, max = 11)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;

    private String role;
}
