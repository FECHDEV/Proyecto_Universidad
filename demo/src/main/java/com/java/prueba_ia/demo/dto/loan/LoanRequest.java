package com.java.prueba_ia.demo.dto.loan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequest {
    @NotNull
    private Long bookId;
}
