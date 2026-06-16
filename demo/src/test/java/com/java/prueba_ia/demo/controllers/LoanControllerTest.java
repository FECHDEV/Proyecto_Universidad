package com.java.prueba_ia.demo.controllers;

import com.java.prueba_ia.demo.dto.loan.LoanRequest;
import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import com.java.prueba_ia.demo.dto.loan.LoanScanRequest;
import com.java.prueba_ia.demo.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private Authentication authentication;

    private LoanController loanController;

    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        loanController = new LoanController(loanService);

        loanResponse = LoanResponse.builder()
                .id(1L)
                .userId(1L)
                .username("testuser")
                .bookId(1L)
                .bookTitulo("Cien Años de Soledad")
                .estado("ACTIVO")
                .vencido(false)
                .build();

        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void findAll_ShouldReturnPage() {
        Page<LoanResponse> page = new PageImpl<>(List.of(loanResponse));
        when(loanService.findAll(any(PageRequest.class), anyString(), anyCollection())).thenReturn(page);

        ResponseEntity<Page<LoanResponse>> result = loanController.findAll(PageRequest.of(0, 10), authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void create_ShouldReturn201() {
        LoanRequest request = new LoanRequest();
        request.setBookId(1L);

        when(loanService.create(any(LoanRequest.class), anyString())).thenReturn(loanResponse);

        ResponseEntity<LoanResponse> result = loanController.create(request, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Cien Años de Soledad", result.getBody().getBookTitulo());
    }

    @Test
    void devolver_ShouldReturn200() {
        when(loanService.devolver(anyLong(), anyString(), anyCollection())).thenReturn(loanResponse);

        ResponseEntity<LoanResponse> result = loanController.devolver(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ACTIVO", result.getBody().getEstado());
    }

    @Test
    void extender_ShouldReturn200() {
        when(loanService.solicitarExtension(anyLong(), anyString(), anyCollection())).thenReturn(loanResponse);

        ResponseEntity<LoanResponse> result = loanController.extender(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ACTIVO", result.getBody().getEstado());
    }

    @Test
    void createByQr_ShouldReturn201() {
        LoanScanRequest scanRequest = new LoanScanRequest();
        scanRequest.setCodigoQr("test-uuid");

        when(loanService.createByQr(any(LoanScanRequest.class), anyString())).thenReturn(loanResponse);

        ResponseEntity<LoanResponse> result = loanController.createByQr(scanRequest, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Cien Años de Soledad", result.getBody().getBookTitulo());
    }

    @Test
    void devolverByQr_ShouldReturn200() {
        LoanScanRequest scanRequest = new LoanScanRequest();
        scanRequest.setCodigoQr("test-uuid");

        when(loanService.devolverByQr(any(LoanScanRequest.class), anyString(), anyCollection())).thenReturn(loanResponse);

        ResponseEntity<LoanResponse> result = loanController.devolverByQr(scanRequest, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ACTIVO", result.getBody().getEstado());
    }
}
