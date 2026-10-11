package edu.final_year.backendwebm.store;

import edu.final_year.backendwebm.exception.CommuneNotFoundException;
import edu.final_year.backendwebm.exception.NifAlreadyRegisteredException;
import edu.final_year.backendwebm.exception.StoreNotFoundException;
import edu.final_year.backendwebm.obr.ObrClient;
import edu.final_year.backendwebm.province.Commune;
import edu.final_year.backendwebm.province.CommuneRepository;
import edu.final_year.backendwebm.store.dto.CreateStoreRequest;
import edu.final_year.backendwebm.store.dto.StoreResponse;
import edu.final_year.backendwebm.user.User;
import edu.final_year.backendwebm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final CommuneRepository communeRepository;
    private final ObrClient obrClient;

    // Deliberately not @Transactional: no DB transaction should stay open during the remote OBR calls.
    public StoreResponse create(String userEmail, CreateStoreRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userEmail));

        String nif = request.nif().trim();

        storeRepository.findByNif(nif).ifPresent(existing -> {
            throw NifAlreadyRegisteredException.forOwner(existing.getUser().getId().equals(user.getId()));
        });

        Commune commune = communeRepository.findById(request.communeId())
                .orElseThrow(CommuneNotFoundException::new);

        String fiscalName = obrClient.checkTin(nif);

        Store store = new Store();
        store.setName(request.name().trim());
        store.setNif(nif);
        store.setFiscalName(fiscalName);
        store.setCommune(commune);
        store.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        store.setCurrency(request.currency());
        store.setUser(user);

        try {
            return StoreResponse.from(storeRepository.save(store));
        } catch (DataIntegrityViolationException e) {
            throw NifAlreadyRegisteredException.forOwner(false);
        }
    }

    /**
     * Resolves a store the caller must own. Not owned and not found are both reported as
     * {@link StoreNotFoundException} so a caller can't tell the two apart.
     */
    public Store findOwnedStore(UUID storeId, String userEmail) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        if (!store.getUser().getEmail().equals(userEmail)) {
            throw new StoreNotFoundException();
        }
        return store;
    }
}
