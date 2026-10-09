# Vault setup

The app encrypts store NIFs via Vault's `transit` secrets engine (`NifEncryptionService`,
`TransitEncryptionService`). Vault doesn't mount `transit` or create any keys by default, so a
fresh (or reset) dev Vault needs one-time setup before the app can start handling stores:

```
VAULT_ADDR=http://localhost:8200 VAULT_TOKEN=myroot ./vault/setup.sh
```

This mounts the `transit` secrets engine and creates the convergent key the app expects
(`nif` by default, overridable via `VAULT_NIF_TRANSIT_KEY`, matching `app.vault.nif-transit-key`
in `application.yaml`). Convergent encryption (`derived=true`, `convergent_encryption=true`)
means the same NIF always encrypts to the same ciphertext, which is what lets
`stores.encrypted_nif` stay queryable by equality and unique.

The script is idempotent — safe to re-run; it skips steps that are already done.

Symptom if this hasn't been run: `StoreService.create` (or anything else that calls
`NifEncryptionService.encrypt`) fails with:

```
VaultClientResponseException: Status 404 Not Found [transit/encrypt/nif]:
no handler for route "transit/encrypt/nif". route entry not found.
```
