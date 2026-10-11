package edu.final_year.backendwebm.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByStoreId(UUID storeId);

    Optional<Product> findByIdAndStoreId(UUID id, UUID storeId);
}
