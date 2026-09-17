package edu.final_year.backendwebm.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

    void deleteAllByExpiresAtBefore(Instant instant);
}
