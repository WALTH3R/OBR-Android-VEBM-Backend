package edu.final_year.backendwebm.sale.dto;

import edu.final_year.backendwebm.sale.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 100, message = "Reference must be at most 100 characters")
        String reference
) {
}
