package edu.final_year.backendwebm.sale.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateSaleRequest(

        UUID clientId,

        @NotEmpty(message = "At least one item is required")
        @Valid
        List<SaleItemRequest> items,

        @NotNull(message = "Payment is required")
        @Valid
        PaymentRequest payment
) {
}
