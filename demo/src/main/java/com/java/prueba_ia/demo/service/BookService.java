package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.book.BookRequest;
import com.java.prueba_ia.demo.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> findAll(Pageable pageable);
    BookResponse findById(Long id);
    BookResponse create(BookRequest request);
    BookResponse update(Long id, BookRequest request);
    void delete(Long id);
}
