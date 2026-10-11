package edu.final_year.backendwebm.store.dto;

import edu.final_year.backendwebm.store.Currency;
import edu.final_year.backendwebm.store.Store;

import java.time.Instant;
import java.util.UUID;

public record StoreResponse(UUID id, String name, String nif, String fiscalName, String obrUsername, UUID communeId,
                            String communeName, UUID provinceId, String provinceName, String phone,
                            Currency currency, Instant createdAt) {

    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getNif(),
                store.getFiscalName(),
                store.getObrUsername(),
                store.getCommune().getId(),
                store.getCommune().getName(),
                store.getCommune().getProvince().getId(),
                store.getCommune().getProvince().getName(),
                store.getPhone(),
                store.getCurrency(),
                store.getCreatedAt()
        );
    }
}
