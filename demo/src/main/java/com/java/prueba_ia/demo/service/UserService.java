package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.user.UserResponse;
import com.java.prueba_ia.demo.dto.user.UserUpdateRequest;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id, String username, Collection<? extends GrantedAuthority> authorities);
    UserResponse update(Long id, UserUpdateRequest request, String username, Collection<? extends GrantedAuthority> authorities);
    void delete(Long id);
}
