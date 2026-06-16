package com.java.prueba_ia.demo.dto.loan;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanScanRequest {
    @NotBlank
    private String codigoQr;
}
