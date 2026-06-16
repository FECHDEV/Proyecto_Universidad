package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.config.QrGenerator;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final LoanRepository loanRepository;
    private final QrGenerator qrGenerator;

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
        book.setCodigoQr(UUID.randomUUID().toString());
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
        if (loanRepository.existsByBookIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new IllegalStateException("No se puede eliminar el libro porque tiene préstamos activos");
        }
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getQRImage(Long id) {
        Book book = findBook(id);
        return qrGenerator.generateQRImage(book.getCodigoQr());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findByQrCode(String codigoQr) {
        Book book = bookRepository.findByCodigoQr(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con código QR: " + codigoQr));
        return bookMapper.toResponse(book);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

}
