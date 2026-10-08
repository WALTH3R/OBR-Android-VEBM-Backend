package edu.final_year.backendwebm.vault;

import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.VaultTransitContext;

import java.nio.charset.StandardCharsets;

/**
 * Wraps Vault's transit secrets engine. Callers pass a fixed derivation context per field
 * (not per value) so that convergent encryption yields the same ciphertext for the same
 * plaintext, keeping equality lookups and unique constraints usable on encrypted columns.
 */
@Service
public class TransitEncryptionService {

    private final VaultTransitOperations transitOperations;

    public TransitEncryptionService(VaultOperations vaultOperations) {
        this.transitOperations = vaultOperations.opsForTransit();
    }

    public String encrypt(String keyName, String context, String plaintext) {
        VaultTransitContext transitContext = transitContext(context);
        Ciphertext ciphertext = transitOperations.encrypt(keyName, Plaintext.of(plaintext, StandardCharsets.UTF_8).with(transitContext));
        return ciphertext.getCiphertext();
    }

    public String decrypt(String keyName, String context, String ciphertext) {
        VaultTransitContext transitContext = transitContext(context);
        Plaintext plaintext = transitOperations.decrypt(keyName, Ciphertext.of(ciphertext).with(transitContext));
        return plaintext.asString(StandardCharsets.UTF_8);
    }

    private VaultTransitContext transitContext(String context) {
        return VaultTransitContext.fromContext(context.getBytes(StandardCharsets.UTF_8));
    }
}
