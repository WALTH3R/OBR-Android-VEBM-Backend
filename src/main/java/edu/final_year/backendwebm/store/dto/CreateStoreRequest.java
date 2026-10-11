package edu.final_year.backendwebm.store.dto;

import edu.final_year.backendwebm.store.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateStoreRequest(

        @NotBlank(message = "Store name is required")
        @Size(max = 150, message = "Store name must be at most 150 characters")
        String name,

        @NotBlank(message = "NIF is required")
        @Size(max = 30, message = "NIF must be at most 30 characters")
        String nif,

        @NotBlank(message = "OBR username is required")
        @Size(max = 100, message = "OBR username must be at most 100 characters")
        String obrUsername,

        @NotBlank(message = "OBR password is required")
        @Size(max = 100, message = "OBR password must be at most 100 characters")
        String obrPassword,

        @NotNull(message = "Commune is required")
        UUID communeId,

        @Size(max = 30, message = "Phone number must be at most 30 characters")
        String phone,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}
