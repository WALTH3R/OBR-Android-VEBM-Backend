package edu.final_year.backendwebm.sale;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    List<Sale> findAllByStoreId(UUID storeId);

    Optional<Sale> findByIdAndStoreId(UUID id, UUID storeId);
}
