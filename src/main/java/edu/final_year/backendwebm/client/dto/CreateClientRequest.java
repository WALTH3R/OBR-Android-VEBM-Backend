package edu.final_year.backendwebm.client.dto;

import edu.final_year.backendwebm.client.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(

        @NotBlank(message = "Client name is required")
        @Size(max = 150, message = "Client name must be at most 150 characters")
        String name,

        @NotNull(message = "Client type is required")
        ClientType clientType,

        @Size(max = 30, message = "NIF must be at most 30 characters")
        String nif,

        @Size(max = 30, message = "Phone number must be at most 30 characters")
        String phone,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address
) {
}
