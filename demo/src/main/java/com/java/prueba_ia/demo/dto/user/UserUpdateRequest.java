package com.java.prueba_ia.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;
}
