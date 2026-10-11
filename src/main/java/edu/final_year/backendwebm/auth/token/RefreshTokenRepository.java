package edu.final_year.backendwebm.auth.token;

import edu.final_year.backendwebm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);

    void deleteAllByExpiresAtBefore(Instant instant);
}
