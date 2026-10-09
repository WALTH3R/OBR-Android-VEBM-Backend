package edu.final_year.backendwebm.client;

import edu.final_year.backendwebm.client.dto.ClientResponse;
import edu.final_year.backendwebm.client.dto.CreateClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeId}/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@PathVariable UUID storeId,
                                                   @Valid @RequestBody CreateClientRequest request,
                                                   Authentication authentication) {
        ClientResponse response = clientService.create(storeId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ClientResponse> list(@PathVariable UUID storeId, Authentication authentication) {
        return clientService.list(storeId, authentication.getName());
    }

    @GetMapping("/{clientId}")
    public ClientResponse get(@PathVariable UUID storeId, @PathVariable UUID clientId, Authentication authentication) {
        return clientService.get(storeId, clientId, authentication.getName());
    }

    @PutMapping("/{clientId}")
    public ClientResponse update(@PathVariable UUID storeId,
                                  @PathVariable UUID clientId,
                                  @Valid @RequestBody CreateClientRequest request,
                                  Authentication authentication) {
        return clientService.update(storeId, clientId, authentication.getName(), request);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable UUID storeId,
                                        @PathVariable UUID clientId,
                                        Authentication authentication) {
        clientService.delete(storeId, clientId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
