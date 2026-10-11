package edu.final_year.backendwebm.province;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProvinceRepository extends JpaRepository<Province, UUID> {
}
