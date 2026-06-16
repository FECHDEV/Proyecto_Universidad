package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.book.BookRequest;
import com.java.prueba_ia.demo.dto.book.BookResponse;
import com.java.prueba_ia.demo.entity.Book;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.mapper.BookMapper;
import com.java.prueba_ia.demo.repository.BookRepository;
import com.java.prueba_ia.demo.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequest bookRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .titulo("Cien Años de Soledad")
                .autor("Gabriel García Márquez")
                .isbn("978-3-16-148410-0")
                .genero("Novela")
                .anioPublicacion(1967)
                .ejemplaresDisponibles(5)
                .build();

        bookRequest = new BookRequest();
        bookRequest.setTitulo("Cien Años de Soledad");
        bookRequest.setAutor("Gabriel García Márquez");
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setGenero("Novela");
        bookRequest.setAnioPublicacion(1967);
        bookRequest.setEjemplaresDisponibles(5);

        bookResponse = BookResponse.builder()
                .id(1L)
                .titulo("Cien Años de Soledad")
                .autor("Gabriel García Márquez")
                .isbn("978-3-16-148410-0")
                .genero("Novela")
                .anioPublicacion(1967)
                .ejemplaresDisponibles(5)
                .build();
    }

    @Test
    void findAll_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

        Page<BookResponse> result = bookService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponse, result.getContent().getFirst());
    }

    @Test
    void findById_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.findById(1L);

        assertNotNull(result);
        assertEquals("Cien Años de Soledad", result.getTitulo());
    }

    @Test
    void findById_NotFound_ShouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    void create_ShouldSucceed() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookMapper.toEntity(any(BookRequest.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.create(bookRequest);

        assertNotNull(result);
        assertEquals("Cien Años de Soledad", result.getTitulo());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void create_DuplicateIsbn_ShouldThrow() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> bookService.create(bookRequest));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_ShouldSucceed() {
        Book updatedBook = Book.builder()
                .id(1L)
                .titulo("Updated Title")
                .autor("Updated Author")
                .isbn("978-3-16-148410-0")
                .ejemplaresDisponibles(3)
                .build();

        BookResponse updatedResponse = BookResponse.builder()
                .id(1L)
                .titulo("Updated Title")
                .autor("Updated Author")
                .isbn("978-3-16-148410-0")
                .ejemplaresDisponibles(3)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(updatedResponse);

        BookResponse result = bookService.update(1L, bookRequest);

        assertNotNull(result);
        assertEquals(updatedResponse, result);
        verify(bookMapper).updateEntity(book, bookRequest);
    }

    @Test
    void update_NotFound_ShouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.update(99L, bookRequest));
    }

    @Test
    void delete_ShouldSucceed() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookId(1L)).thenReturn(List.of());

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_WithActiveLoans_ShouldThrow() {
        Loan activeLoan = Loan.builder().id(1L).estado(EstadoPrestamo.ACTIVO).book(book).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookId(1L)).thenReturn(List.of(activeLoan));

        assertThrows(IllegalStateException.class, () -> bookService.delete(1L));
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void delete_NotFound_ShouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(99L));
    }
}
