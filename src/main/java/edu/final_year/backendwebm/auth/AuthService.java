package edu.final_year.backendwebm.auth;

import edu.final_year.backendwebm.auth.dto.SignupRequest;
import edu.final_year.backendwebm.auth.dto.UserResponse;
import edu.final_year.backendwebm.auth.jwt.JwtService;
import edu.final_year.backendwebm.auth.token.RefreshTokenService;
import edu.final_year.backendwebm.auth.token.RevokedToken;
import edu.final_year.backendwebm.auth.token.RevokedTokenRepository;
import edu.final_year.backendwebm.exception.EmailAlreadyExistsException;
import edu.final_year.backendwebm.user.User;
import edu.final_year.backendwebm.user.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Transactional
    public void logout(String accessToken) {
        Claims claims = jwtService.parseClaims(accessToken);

        revokedTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
        revokedTokenRepository.save(new RevokedToken(claims.getId(), claims.getExpiration().toInstant()));

        User user = userRepository.findByEmail(claims.getSubject())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + claims.getSubject()));
        refreshTokenService.revokeAllForUser(user);
    }
}
