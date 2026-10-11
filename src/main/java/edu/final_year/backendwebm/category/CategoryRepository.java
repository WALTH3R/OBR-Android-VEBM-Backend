package edu.final_year.backendwebm.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByStoreId(UUID storeId);

    Optional<Category> findByIdAndStoreId(UUID id, UUID storeId);
}
