package edu.final_year.backendwebm.store.dto;

import edu.final_year.backendwebm.store.Currency;
import edu.final_year.backendwebm.store.Store;

import java.time.Instant;
import java.util.UUID;

public record StoreResponse(UUID id, String name, String nif, String fiscalName, UUID communeId,
                            String communeName, UUID provinceId, String provinceName, String phone,
                            Currency currency, Instant createdAt) {

    /**
     * @param nif the plaintext NIF; the caller must decrypt it first, since
     *            {@link Store#getEncryptedNif()} only ever holds Vault ciphertext.
     */
    public static StoreResponse from(Store store, String nif) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                nif,
                store.getFiscalName(),
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
