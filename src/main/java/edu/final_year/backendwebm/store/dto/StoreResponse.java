package edu.final_year.backendwebm.store.dto;

import edu.final_year.backendwebm.store.Currency;
import edu.final_year.backendwebm.store.Store;

import java.time.Instant;

public record StoreResponse(Long id, String name, String nif, String fiscalName, String town, String phone,
                            Currency currency, Instant createdAt) {

    public static StoreResponse from(Store store) {
        return new StoreResponse(store.getId(), store.getName(), store.getNif(), store.getFiscalName(),
                store.getTown(), store.getPhone(), store.getCurrency(), store.getCreatedAt());
    }
}
