package edu.final_year.backendwebm.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByStoreId(Long storeId);

    Optional<Product> findByIdAndStoreId(Long id, Long storeId);
}
