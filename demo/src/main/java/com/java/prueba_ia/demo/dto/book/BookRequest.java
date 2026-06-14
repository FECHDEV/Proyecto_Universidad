package com.java.prueba_ia.demo.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    @NotBlank
    private String isbn;

    private String genero;

    private Integer anioPublicacion;

    @NotNull
    @PositiveOrZero
    private Integer ejemplaresDisponibles;
}
