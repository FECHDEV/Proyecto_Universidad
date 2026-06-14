package com.java.prueba_ia.demo.repository;

import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByEstado(EstadoPrestamo estado);
}
