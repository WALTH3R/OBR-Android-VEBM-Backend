package edu.final_year.backendwebm.province;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommuneRepository extends JpaRepository<Commune, UUID> {

    List<Commune> findAllByProvinceId(UUID provinceId);
}
