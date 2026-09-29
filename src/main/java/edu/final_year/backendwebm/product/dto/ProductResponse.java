package edu.final_year.backendwebm.product.dto;

import edu.final_year.backendwebm.product.Product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, Long categoryId, String categoryName,
                               String unitOfMeasurement, BigDecimal costPerUnit, BigDecimal sellPricePerUnit,
                               Long tvaId, String tvaName) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getUnitOfMeasurement(),
                product.getCostPerUnit(),
                product.getSellPricePerUnit(),
                product.getTva().getId(),
                product.getTva().getName()
        );
    }
}
