package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    void delete(Long id);
}
