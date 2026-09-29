package edu.final_year.backendwebm.store.dto;

import edu.final_year.backendwebm.store.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(

        @NotBlank(message = "Store name is required")
        @Size(max = 150, message = "Store name must be at most 150 characters")
        String name,

        @NotBlank(message = "NIF is required")
        @Size(max = 30, message = "NIF must be at most 30 characters")
        String nif,

        @NotBlank(message = "Town/county is required")
        @Size(max = 150, message = "Town/county must be at most 150 characters")
        String town,

        @Size(max = 30, message = "Phone number must be at most 30 characters")
        String phone,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}
