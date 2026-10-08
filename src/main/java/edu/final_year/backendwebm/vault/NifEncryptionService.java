package edu.final_year.backendwebm.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Encrypts/decrypts the store NIF via Vault transit. Uses a fixed derivation context (not
 * per-value) so the same NIF always encrypts to the same ciphertext, keeping
 * {@code StoreRepository.findByNif} and the {@code stores.nif} unique constraint working
 * against the encrypted column.
 */
@Service
public class NifEncryptionService {

    private static final String CONTEXT = "store-nif";

    private final TransitEncryptionService transitEncryptionService;
    private final String keyName;

    public NifEncryptionService(TransitEncryptionService transitEncryptionService,
                                 @Value("${app.vault.nif-transit-key}") String keyName) {
        this.transitEncryptionService = transitEncryptionService;
        this.keyName = keyName;
    }

    public String encrypt(String nif) {
        return transitEncryptionService.encrypt(keyName, CONTEXT, nif);
    }

    public String decrypt(String encryptedNif) {
        return transitEncryptionService.decrypt(keyName, CONTEXT, encryptedNif);
    }
}
