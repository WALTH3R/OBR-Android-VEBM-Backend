package edu.final_year.backendwebm.auth;

import edu.final_year.backendwebm.auth.dto.LoginRequest;
import edu.final_year.backendwebm.auth.dto.LoginResponse;
import edu.final_year.backendwebm.auth.dto.RefreshRequest;
import edu.final_year.backendwebm.auth.dto.SignupRequest;
import edu.final_year.backendwebm.auth.dto.UserResponse;
import edu.final_year.backendwebm.auth.jwt.JwtService;
import edu.final_year.backendwebm.auth.token.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup endpoint called for email={}", request.email());
        UserResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called for email={}", request.email());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshTokenService.RotatedTokens rotated = refreshTokenService.rotate(request.refreshToken());
        String accessToken = jwtService.generateToken(rotated.user().getEmail());

        return ResponseEntity.ok(LoginResponse.of(accessToken, rotated.rawToken(), UserResponse.from(rotated.user())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("Logout endpoint called");
        authService.logout(authHeader.substring(BEARER_PREFIX.length()));
        return ResponseEntity.ok().build();
    }
}
