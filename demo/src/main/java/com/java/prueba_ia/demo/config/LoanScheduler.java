package com.java.prueba_ia.demo.config;

import com.java.prueba_ia.demo.entity.EstadoPrestamo;
import com.java.prueba_ia.demo.entity.Loan;
import com.java.prueba_ia.demo.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanScheduler {

    private final LoanRepository loanRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void marcarVencidos() {
        List<Loan> vencidos = loanRepository
                .findAllByEstadoAndFechaMaximaDevolucionBefore(EstadoPrestamo.ACTIVO, LocalDateTime.now());

        for (Loan loan : vencidos) {
            loan.setEstado(EstadoPrestamo.VENCIDO);
            log.info("Préstamo {} marcado como VENCIDO", loan.getId());
        }

        if (!vencidos.isEmpty()) {
            loanRepository.saveAll(vencidos);
        }
    }
}
