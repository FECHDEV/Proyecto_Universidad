package com.java.prueba_ia.demo.service;

import com.java.prueba_ia.demo.dto.loan.LoanRequest;
import com.java.prueba_ia.demo.dto.loan.LoanResponse;
import com.java.prueba_ia.demo.entity.Book;
import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import com.java.prueba_ia.demo.entity.User;
import com.java.prueba_ia.demo.exceptions.ResourceNotFoundException;
import com.java.prueba_ia.demo.mapper.LoanMapper;
import com.java.prueba_ia.demo.repository.BookRepository;
import com.java.prueba_ia.demo.repository.LoanRepository;
import com.java.prueba_ia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<LoanResponse> findAll(Pageable pageable, String username, Collection<? extends GrantedAuthority> authorities) {
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return loanRepository.findAll(pageable).map(loanMapper::toResponse);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<Loan> userLoans = loanRepository.findByUserId(user.getId());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userLoans.size());
        if (start > userLoans.size()) {
            return Page.empty(pageable);
        }
        List<LoanResponse> content = userLoans.subList(start, end).stream()
                .map(loanMapper::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, userLoans.size());
    }

    @Override
    @Transactional
    public LoanResponse create(LoanRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.getBookId()));

        if (book.getEjemplaresDisponibles() <= 0) {
            throw new IllegalArgumentException("No hay ejemplares disponibles para este libro");
        }

        book.setEjemplaresDisponibles(book.getEjemplaresDisponibles() - 1);

        LocalDateTime now = LocalDateTime.now();

        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .fechaPrestamo(now)
                .fechaMaximaDevolucion(now.plusDays(7))
                .extensiones(0)
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        loanRepository.save(loan);

        return loanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponse devolver(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));

        verifyOwnershipOrAdmin(loan, username, authorities);

        if (loan.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo ya fue devuelto o está vencido");
        }

        loan.setEstado(EstadoPrestamo.DEVUELTO);
        loan.setFechaDevolucion(LocalDateTime.now());

        Book book = loan.getBook();
        book.setEjemplaresDisponibles(book.getEjemplaresDisponibles() + 1);

        loanRepository.save(loan);

        return loanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponse solicitarExtension(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));

        verifyOwnershipOrAdmin(loan, username, authorities);

        if (loan.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalStateException("Solo se puede extender un préstamo activo");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fechaMax = loan.getFechaMaximaDevolucion();

        if (now.isBefore(fechaMax.minusDays(1).toLocalDate().atStartOfDay()) || now.isAfter(fechaMax)) {
            throw new IllegalStateException("Solo puedes solicitar extensión en el último día disponible");
        }

        Book book = loan.getBook();
        if (book.getEjemplaresDisponibles() == 0 && loan.getExtensiones() >= 1) {
            throw new IllegalStateException("No hay copias disponibles para extender más de una vez");
        }

        loan.setFechaMaximaDevolucion(fechaMax.plusDays(7));
        loan.setExtensiones(loan.getExtensiones() + 1);
        loanRepository.save(loan);

        return loanMapper.toResponse(loan);
    }

    private void verifyOwnershipOrAdmin(Loan loan, String username, Collection<? extends GrantedAuthority> authorities) {
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !Objects.equals(loan.getUser().getUsername(), username)) {
            throw new IllegalArgumentException("No tienes permiso para modificar este préstamo");
        }
    }

}
