package com.java.prueba_ia.demo.mapper;

import com.java.prueba_ia.demo.dto.book.BookRequest;
import com.java.prueba_ia.demo.dto.book.BookResponse;
import com.java.prueba_ia.demo.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "codigoQr", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(target = "codigoQr", ignore = true)
    void updateEntity(@MappingTarget Book book, BookRequest request);

    BookResponse toResponse(Book book);
}
