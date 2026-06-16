package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.loan.LoanRequest;
import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import com.java.prueba_ia.demo.dto.loan.LoanScanRequest;
import com.java.prueba_ia.demo.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<Page<LoanResponse>> findAll(@PageableDefault(size = 10) Pageable pageable,
                                                       Authentication authentication) {
        return ResponseEntity.ok(loanService.findAll(pageable, authentication.getName(), authentication.getAuthorities()));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanRequest request,
                                                Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.create(request, username));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<LoanResponse> devolver(@PathVariable Long id,
                                                  Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(loanService.devolver(id, username, authentication.getAuthorities()));
    }

    @PutMapping("/{id}/extender")
    public ResponseEntity<LoanResponse> extender(@PathVariable Long id,
                                                  Authentication authentication) {
        return ResponseEntity.ok(loanService.solicitarExtension(id, authentication.getName(), authentication.getAuthorities()));
    }

    @PostMapping("/scan")
    public ResponseEntity<LoanResponse> createByQr(@Valid @RequestBody LoanScanRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createByQr(request, authentication.getName()));
    }

    @PostMapping("/scan-devolver")
    public ResponseEntity<LoanResponse> devolverByQr(@Valid @RequestBody LoanScanRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(
                loanService.devolverByQr(request, authentication.getName(), authentication.getAuthorities()));
    }
}
