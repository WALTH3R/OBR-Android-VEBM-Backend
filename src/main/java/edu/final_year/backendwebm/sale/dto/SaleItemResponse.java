package edu.final_year.backendwebm.sale.dto;

import edu.final_year.backendwebm.sale.SaleItem;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemResponse(UUID id, UUID productId, String productName, String unitOfMeasurement,
                                int quantity, BigDecimal unitPrice, BigDecimal tvaRate, BigDecimal lineSubtotal,
                                BigDecimal lineTvaAmount, BigDecimal lineTotal) {

    public static SaleItemResponse from(SaleItem item) {
        return new SaleItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getUnitOfMeasurement(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTvaRate(),
                item.getLineSubtotal(),
                item.getLineTvaAmount(),
                item.getLineTotal()
        );
    }
}
