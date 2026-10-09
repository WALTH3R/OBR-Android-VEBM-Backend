package edu.final_year.backendwebm.client;

import edu.final_year.backendwebm.client.dto.ClientResponse;
import edu.final_year.backendwebm.client.dto.CreateClientRequest;
import edu.final_year.backendwebm.exception.ClientNotFoundException;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final StoreService storeService;

    public ClientResponse create(UUID storeId, String userEmail, CreateClientRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Client client = new Client();
        client.setStore(store);
        applyRequest(client, request);

        return ClientResponse.from(clientRepository.save(client));
    }

    public List<ClientResponse> list(UUID storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return clientRepository.findAllByStoreId(storeId).stream()
                .map(ClientResponse::from)
                .toList();
    }

    public ClientResponse get(UUID storeId, UUID clientId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return ClientResponse.from(findOwnedClient(storeId, clientId));
    }

    @Transactional
    public ClientResponse update(UUID storeId, UUID clientId, String userEmail, CreateClientRequest request) {
        storeService.findOwnedStore(storeId, userEmail);
        Client client = findOwnedClient(storeId, clientId);

        applyRequest(client, request);

        return ClientResponse.from(clientRepository.save(client));
    }

    @Transactional
    public void delete(UUID storeId, UUID clientId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);
        Client client = findOwnedClient(storeId, clientId);

        clientRepository.delete(client);
    }

    /** Package-visible so SaleService can resolve the client attached to a sale. */
    Client findOwnedClient(UUID storeId, UUID clientId) {
        return clientRepository.findByIdAndStoreId(clientId, storeId)
                .orElseThrow(ClientNotFoundException::new);
    }

    private void applyRequest(Client client, CreateClientRequest request) {
        client.setName(request.name().trim());
        client.setClientType(request.clientType());
        client.setNif(blankToNull(request.nif()));
        client.setPhone(blankToNull(request.phone()));
        client.setAddress(blankToNull(request.address()));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
