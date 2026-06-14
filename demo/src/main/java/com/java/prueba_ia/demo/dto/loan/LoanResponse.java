package com.java.prueba_ia.demo.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitulo;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private String estado;
    private boolean vencido;
    private LocalDateTime createdAt;
}
