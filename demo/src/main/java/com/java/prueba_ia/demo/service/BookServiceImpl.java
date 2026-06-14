package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.book.BookRequest;
import com.java.prueba_ia.demo.dto.book.BookResponse;
import com.java.prueba_ia.demo.entity.Book;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.mapper.BookMapper;
import com.java.prueba_ia.demo.repository.BookRepository;
import com.java.prueba_ia.demo.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return bookMapper.toResponse(findBook(id));
    }

    @Override
    @Transactional
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("El ISBN ya existe");
        }

        Book book = bookMapper.toEntity(request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = findBook(id);

        bookMapper.updateEntity(book, request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = findBook(id);
        boolean hasActiveLoans = loanRepository.findByBookId(id).stream()
                .anyMatch(loan -> loan.getEstado() == EstadoPrestamo.ACTIVO);
        if (hasActiveLoans) {
            throw new IllegalStateException("No se puede eliminar el libro porque tiene préstamos activos");
        }
        bookRepository.delete(book);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

}
