package edu.final_year.backendwebm.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Product name must be at most 150 characters")
        String name,

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotBlank(message = "Unit of measurement is required")
        @Size(max = 30, message = "Unit of measurement must be at most 30 characters")
        String unitOfMeasurement,

        @NotNull(message = "Cost per unit is required")
        @DecimalMin(value = "0.0", message = "Cost per unit must not be negative")
        BigDecimal costPerUnit,

        @NotNull(message = "Sell price per unit is required")
        @DecimalMin(value = "0.0", message = "Sell price per unit must not be negative")
        BigDecimal sellPricePerUnit,

        @NotNull(message = "TVA rate is required")
        UUID tvaId
) {
}
