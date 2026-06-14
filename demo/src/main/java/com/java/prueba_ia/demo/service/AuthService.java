package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.auth.AuthResponse;
import com.java.prueba_ia.demo.dto.auth.LoginRequest;
import com.java.prueba_ia.demo.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
