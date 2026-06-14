package com.java.prueba_ia.demo.mapper;

import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitulo", source = "book.titulo")
    @Mapping(target = "estado", expression = "java(loan.getEstado().name())")
    @Mapping(target = "vencido", ignore = true)
    LoanResponse toResponse(Loan loan);

    @AfterMapping
    default void setVencido(Loan loan, @MappingTarget LoanResponse response) {
        response.setVencido(
            loan.getEstado() == EstadoPrestamo.ACTIVO
            && loan.getFechaMaximaDevolucion() != null
            && loan.getFechaMaximaDevolucion().isBefore(LocalDateTime.now())
        );
    }
}
