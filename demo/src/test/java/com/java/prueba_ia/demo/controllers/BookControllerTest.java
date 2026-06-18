package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.book.BookRequest;
import com.java.prueba_ia.demo.dto.book.BookResponse;
import com.java.prueba_ia.demo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    private BookController bookController;

    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        bookController = new BookController(bookService);

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
        Page<BookResponse> page = new PageImpl<>(List.of(bookResponse));
        when(bookService.findAll(any(PageRequest.class))).thenReturn(page);

        ResponseEntity<Page<BookResponse>> result = bookController.findAll(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void findById_ShouldReturnBook() {
        when(bookService.findById(1L)).thenReturn(bookResponse);

        ResponseEntity<BookResponse> result = bookController.findById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cien Años de Soledad", result.getBody().getTitulo());
    }

    @Test
    void create_ShouldReturn201() {
        BookRequest request = new BookRequest();
        when(bookService.create(any(BookRequest.class))).thenReturn(bookResponse);

        ResponseEntity<BookResponse> result = bookController.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void update_ShouldReturn200() {
        BookRequest request = new BookRequest();
        when(bookService.update(any(Long.class), any(BookRequest.class))).thenReturn(bookResponse);

        ResponseEntity<BookResponse> result = bookController.update(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void delete_ShouldReturn204() {
        ResponseEntity<Void> result = bookController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(bookService).delete(1L);
    }

    @Test
    void findByQrCode_ShouldReturnBook() {
        when(bookService.findByQrCode("test-uuid")).thenReturn(bookResponse);

        ResponseEntity<BookResponse> result = bookController.findByQrCode("test-uuid");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cien Años de Soledad", result.getBody().getTitulo());
    }

    @Test
    void getQR_ShouldReturnImagePng() {
        byte[] qrImage = new byte[]{1, 2, 3, 4};
        when(bookService.getQRImage(1L)).thenReturn(qrImage);

        ResponseEntity<byte[]> result = bookController.getQR(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, result.getHeaders().getContentType());
        assertArrayEquals(qrImage, result.getBody());
    }
}
