package edu.final_year.backendwebm.client.dto;

import edu.final_year.backendwebm.client.Client;
import edu.final_year.backendwebm.client.ClientType;

import java.time.Instant;
import java.util.UUID;

public record ClientResponse(UUID id, String name, ClientType clientType, String nif, String phone,
                              String address, Instant createdAt) {

    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getClientType(),
                client.getNif(),
                client.getPhone(),
                client.getAddress(),
                client.getCreatedAt()
        );
    }
}
