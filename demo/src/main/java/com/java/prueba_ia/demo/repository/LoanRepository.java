package com.java.prueba_ia.demo.repository;

import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    Page<Loan> findByUserId(Long userId, Pageable pageable);
    List<Loan> findByBookId(Long bookId);
    boolean existsByBookIdAndEstado(Long bookId, EstadoPrestamo estado);
    boolean existsByUserIdAndEstado(Long userId, EstadoPrestamo estado);
    Optional<Loan> findByIdAndUserId(Long id, Long userId);
}
