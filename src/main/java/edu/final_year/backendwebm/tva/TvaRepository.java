package edu.final_year.backendwebm.tva;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TvaRepository extends JpaRepository<Tva, UUID> {
}
