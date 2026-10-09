package edu.final_year.backendwebm.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findAllByStoreId(UUID storeId);

    Optional<Client> findByIdAndStoreId(UUID id, UUID storeId);
}
