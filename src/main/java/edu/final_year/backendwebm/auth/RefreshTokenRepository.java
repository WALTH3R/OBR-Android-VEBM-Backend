package edu.final_year.backendwebm.auth;

import edu.final_year.backendwebm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);

    void deleteAllByExpiresAtBefore(Instant instant);
}
