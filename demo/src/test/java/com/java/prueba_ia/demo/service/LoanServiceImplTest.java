package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.loan.LoanRequest;
import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import com.java.prueba_ia.demo.entity.*;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.mapper.LoanMapper;
import com.java.prueba_ia.demo.repository.BookRepository;
import com.java.prueba_ia.demo.repository.LoanRepository;
import com.java.prueba_ia.demo.repository.UserRepository;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private User user;
    private User otherUser;
    private Book book;
    private Loan activeLoan;
    private LoanResponse loanResponse;
    private LoanRequest loanRequest;
    private Collection<GrantedAuthority> userAuthorities;
    private Collection<GrantedAuthority> adminAuthorities;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded")
                .email("test@example.com")
                .fullName("Test User")
                .role(Role.USER)
                .build();

        otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .password("encoded")
                .email("other@example.com")
                .fullName("Other User")
                .role(Role.USER)
                .build();

        book = Book.builder()
                .id(1L)
                .titulo("Cien Años de Soledad")
                .autor("Gabriel García Márquez")
                .isbn("978-3-16-148410-0")
                .ejemplaresDisponibles(5)
                .build();

        activeLoan = Loan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .fechaPrestamo(LocalDateTime.now().minusDays(3))
                .fechaMaximaDevolucion(LocalDateTime.now().plusDays(4))
                .extensiones(0)
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        loanResponse = LoanResponse.builder()
                .id(1L)
                .userId(1L)
                .username("testuser")
                .bookId(1L)
                .bookTitulo("Cien Años de Soledad")
                .estado("ACTIVO")
                .vencido(false)
                .build();

        loanRequest = new LoanRequest();
        loanRequest.setBookId(1L);

        userAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void findAll_AsAdmin_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> loanPage = new PageImpl<>(List.of(activeLoan));

        when(loanRepository.findAll(pageable)).thenReturn(loanPage);
        when(loanMapper.toResponse(activeLoan)).thenReturn(loanResponse);

        Page<LoanResponse> result = loanService.findAll(pageable, "admin", adminAuthorities);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findAll_AsUser_ShouldReturnOwnLoans() {
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(loanRepository.findByUserId(1L)).thenReturn(List.of(activeLoan));
        when(loanMapper.toResponse(activeLoan)).thenReturn(loanResponse);

        Page<LoanResponse> result = loanService.findAll(pageable, "testuser", userAuthorities);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void create_ShouldSucceed() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.create(loanRequest, "testuser");

        assertNotNull(result);
        assertEquals(4, book.getEjemplaresDisponibles());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void create_NoCopies_ShouldThrow() {
        book.setEjemplaresDisponibles(0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> loanService.create(loanRequest, "testuser"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void create_UserNotFound_ShouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.create(loanRequest, "unknown"));
    }

    @Test
    void devolver_OwnLoan_ShouldSucceed() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.devolver(1L, "testuser", userAuthorities);

        assertNotNull(result);
        assertEquals(EstadoPrestamo.DEVUELTO, activeLoan.getEstado());
        assertEquals(6, book.getEjemplaresDisponibles());
    }

    @Test
    void devolver_AsAdmin_ShouldSucceed() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.devolver(1L, "admin", adminAuthorities);

        assertNotNull(result);
        assertEquals(EstadoPrestamo.DEVUELTO, activeLoan.getEstado());
    }

    @Test
    void devolver_NotOwnLoan_NotAdmin_ShouldThrow() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.devolver(1L, "otheruser", userAuthorities));
    }

    @Test
    void devolver_AlreadyReturned_ShouldThrow() {
        activeLoan.setEstado(EstadoPrestamo.DEVUELTO);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.devolver(1L, "testuser", userAuthorities));
    }

    @Test
    void devolver_NotFound_ShouldThrow() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.devolver(99L, "testuser", userAuthorities));
    }

    @Test
    void solicitarExtension_OwnLoan_ShouldSucceed() {
        activeLoan.setFechaMaximaDevolucion(LocalDateTime.now().plusHours(12));
        book.setEjemplaresDisponibles(1);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.solicitarExtension(1L, "testuser", userAuthorities);

        assertNotNull(result);
        assertEquals(1, activeLoan.getExtensiones());
    }

    @Test
    void solicitarExtension_NotOwnLoan_NotAdmin_ShouldThrow() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.solicitarExtension(1L, "otheruser", userAuthorities));
    }

    @Test
    void solicitarExtension_AsAdmin_ShouldSucceed() {
        activeLoan.setFechaMaximaDevolucion(LocalDateTime.now().plusHours(12));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.solicitarExtension(1L, "admin", adminAuthorities);

        assertNotNull(result);
    }

    @Test
    void solicitarExtension_NotActive_ShouldThrow() {
        activeLoan.setEstado(EstadoPrestamo.DEVUELTO);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalStateException.class,
                () -> loanService.solicitarExtension(1L, "testuser", userAuthorities));
    }

    @Test
    void solicitarExtension_TooEarly_ShouldThrow() {
        activeLoan.setFechaMaximaDevolucion(LocalDateTime.now().plusDays(5));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalStateException.class,
                () -> loanService.solicitarExtension(1L, "testuser", userAuthorities));
    }

    @Test
    void solicitarExtension_TooLate_ShouldThrow() {
        activeLoan.setFechaMaximaDevolucion(LocalDateTime.now().minusDays(1));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));

        assertThrows(IllegalStateException.class,
                () -> loanService.solicitarExtension(1L, "testuser", userAuthorities));
    }
}
