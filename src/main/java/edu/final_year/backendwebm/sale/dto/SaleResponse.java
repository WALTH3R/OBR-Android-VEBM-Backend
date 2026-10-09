package edu.final_year.backendwebm.sale.dto;

import edu.final_year.backendwebm.sale.PaymentMethod;
import edu.final_year.backendwebm.sale.Sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(UUID id, UUID clientId, String clientName, List<SaleItemResponse> items,
                            BigDecimal subtotal, BigDecimal tvaAmount, BigDecimal totalAmount,
                            PaymentMethod paymentMethod, BigDecimal paymentAmount, String paymentReference,
                            BigDecimal changeDue, boolean obrSubmitted, Instant obrSubmittedAt, Instant createdAt) {

    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getClient() == null ? null : sale.getClient().getId(),
                sale.getClient() == null ? null : sale.getClient().getName(),
                sale.getItems().stream().map(SaleItemResponse::from).toList(),
                sale.getSubtotal(),
                sale.getTvaAmount(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getPaymentAmount(),
                sale.getPaymentReference(),
                sale.getPaymentAmount().subtract(sale.getTotalAmount()),
                sale.isObrSubmitted(),
                sale.getObrSubmittedAt(),
                sale.getCreatedAt()
        );
    }
}
