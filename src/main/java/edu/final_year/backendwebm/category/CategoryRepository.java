package edu.final_year.backendwebm.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByStoreId(Long storeId);

    Optional<Category> findByIdAndStoreId(Long id, Long storeId);
}
