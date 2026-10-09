#!/usr/bin/env bash
# One-time local dev setup for the Vault instance this app talks to.
#
# The app's VaultConfig/TransitEncryptionService (see src/main/java/.../vault/) assume a
# `transit` secrets engine is already mounted with a convergent key named `nif`
# (derived=true, convergent_encryption=true). Vault does not come with either by default,
# so a fresh/reset dev Vault container needs this run once before the app can encrypt
# store NIFs. Without it, StoreService.create fails with:
#   VaultClientResponseException: Status 404 Not Found [transit/encrypt/nif]:
#   no handler for route "transit/encrypt/nif". route entry not found.
#
# Usage: VAULT_ADDR=http://localhost:8200 VAULT_TOKEN=myroot ./vault/setup.sh

set -euo pipefail

VAULT_ADDR="${VAULT_ADDR:-http://localhost:8200}"
VAULT_TOKEN="${VAULT_TOKEN:-myroot}"
TRANSIT_KEY="${VAULT_NIF_TRANSIT_KEY:-nif}"

echo "Vault: $VAULT_ADDR"

if curl -fs -H "X-Vault-Token: $VAULT_TOKEN" "$VAULT_ADDR/v1/sys/mounts" | grep -q '"transit/"'; then
  echo "transit secrets engine already mounted"
else
  echo "mounting transit secrets engine"
  curl -fs -X POST -H "X-Vault-Token: $VAULT_TOKEN" \
    -d '{"type":"transit"}' \
    "$VAULT_ADDR/v1/sys/mounts/transit" >/dev/null
fi

if curl -fs -H "X-Vault-Token: $VAULT_TOKEN" "$VAULT_ADDR/v1/transit/keys/$TRANSIT_KEY" >/dev/null 2>&1; then
  echo "transit key '$TRANSIT_KEY' already exists"
else
  echo "creating convergent transit key '$TRANSIT_KEY'"
  curl -fs -X POST -H "X-Vault-Token: $VAULT_TOKEN" \
    -d '{"derived":true,"convergent_encryption":true}' \
    "$VAULT_ADDR/v1/transit/keys/$TRANSIT_KEY" >/dev/null
fi

echo "done"
