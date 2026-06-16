package com.java.prueba_ia.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "libros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false)
    private String autor;

    @NotBlank
    @Column(unique = true)
    private String isbn;

    @Column(unique = true, length = 36)
    private String codigoQr;

    private String genero;

    private Integer anioPublicacion;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer ejemplaresDisponibles;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
