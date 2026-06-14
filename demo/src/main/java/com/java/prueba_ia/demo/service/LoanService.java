package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.loan.LoanRequest;
import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface LoanService {
    Page<LoanResponse> findAll(Pageable pageable, String username, Collection<? extends GrantedAuthority> authorities);
    LoanResponse create(LoanRequest request, String username);
    LoanResponse devolver(Long id, String username);
    LoanResponse solicitarExtension(Long id, String username);
}
