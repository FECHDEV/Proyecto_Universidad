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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private QrGenerator qrGenerator;

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
    void create_ShouldGenerateQrCode() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookMapper.toEntity(any(BookRequest.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.toResponse(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            return BookResponse.builder()
                    .id(b.getId())
                    .titulo(b.getTitulo())
                    .autor(b.getAutor())
                    .isbn(b.getIsbn())
                    .codigoQr(b.getCodigoQr())
                    .ejemplaresDisponibles(b.getEjemplaresDisponibles())
                    .build();
        });

        BookResponse result = bookService.create(bookRequest);

        assertNotNull(result);
        assertNotNull(result.getCodigoQr());
        assertEquals(36, result.getCodigoQr().length());

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        assertNotNull(captor.getValue().getCodigoQr());
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
        when(loanRepository.existsByBookIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(false);

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_WithActiveLoans_ShouldThrow() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bookService.delete(1L));
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void delete_NotFound_ShouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(99L));
    }

    @Test
    void getQRImage_ShouldReturnBytes() {
        book.setCodigoQr("test-uuid-1234");
        byte[] expectedImage = new byte[]{1, 2, 3, 4};

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(qrGenerator.generateQRImage("test-uuid-1234")).thenReturn(expectedImage);

        byte[] result = bookService.getQRImage(1L);

        assertArrayEquals(expectedImage, result);
        verify(qrGenerator).generateQRImage("test-uuid-1234");
    }

    @Test
    void getQRImage_BookNotFound_ShouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getQRImage(99L));
    }

    @Test
    void findByQrCode_ShouldReturnBook() {
        when(bookRepository.findByCodigoQr("test-uuid")).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.findByQrCode("test-uuid");

        assertNotNull(result);
        assertEquals("Cien Años de Soledad", result.getTitulo());
    }

    @Test
    void findByQrCode_NotFound_ShouldThrow() {
        when(bookRepository.findByCodigoQr("invalid-uuid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findByQrCode("invalid-uuid"));
    }
}
