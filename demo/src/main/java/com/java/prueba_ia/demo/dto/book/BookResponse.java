package com.java.prueba_ia.demo.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String codigoQr;
    private String genero;
    private Integer anioPublicacion;
    private Integer ejemplaresDisponibles;
    private LocalDateTime createdAt;
}
