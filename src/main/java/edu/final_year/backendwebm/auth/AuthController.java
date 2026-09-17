package edu.final_year.backendwebm.auth;

import edu.final_year.backendwebm.auth.dto.LoginRequest;
import edu.final_year.backendwebm.auth.dto.LoginResponse;
import edu.final_year.backendwebm.auth.dto.RefreshRequest;
import edu.final_year.backendwebm.auth.dto.SignupRequest;
import edu.final_year.backendwebm.auth.dto.UserResponse;
import edu.final_year.backendwebm.auth.jwt.JwtService;
import edu.final_year.backendwebm.auth.token.RefreshTokenService;
import edu.final_year.backendwebm.user.User;
import edu.final_year.backendwebm.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + request.email()));

        String accessToken = jwtService.generateToken(authentication.getName());
        String refreshToken = refreshTokenService.issue(user);

        return ResponseEntity.ok(LoginResponse.of(accessToken, refreshToken, UserResponse.from(user)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshTokenService.RotatedTokens rotated = refreshTokenService.rotate(request.refreshToken());
        String accessToken = jwtService.generateToken(rotated.user().getEmail());

        return ResponseEntity.ok(LoginResponse.of(accessToken, rotated.rawToken(), UserResponse.from(rotated.user())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        authService.logout(authHeader.substring(BEARER_PREFIX.length()));
        return ResponseEntity.ok().build();
    }
}
