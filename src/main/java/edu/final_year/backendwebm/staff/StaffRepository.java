package edu.final_year.backendwebm.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findAllByStoreId(UUID storeId);

    Optional<Staff> findByIdAndStoreId(UUID id, UUID storeId);
}
