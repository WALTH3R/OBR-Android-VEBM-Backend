# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build: `./mvnw compile`
- Run tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=BackendWebmJavaApplicationTests`
- Run a single test method: `./mvnw test -Dtest=BackendWebmJavaApplicationTests#contextLoads`
- Run the app locally: `./mvnw spring-boot:run` (defaults to port 8080; override with `-Dspring-boot.run.arguments="--server.port=8081"`)
- Package: `./mvnw package`

The app requires a running Postgres instance to start (Spring context eagerly configures a `DataSource`; `spring.jpa.hibernate.ddl-auto=update` auto-migrates the schema on boot). Connection is configured via env vars with local defaults in `src/main/resources/application.yaml`:
`DB_HOST` (default `localhost`), `DB_PORT` (default `5434`), `DB_NAME` (default `backend_webm`), `DB_USERNAME`/`DB_PASSWORD` (default `postgres`/`postgres`).

JWT signing is configured the same way: `JWT_SECRET` (defaults to a dev-only placeholder — **must** be overridden outside local dev), `JWT_EXPIRATION_MINUTES` (default `60`, access token lifetime), and `JWT_REFRESH_EXPIRATION_DAYS` (default `30`, refresh token lifetime).

## Architecture

Spring Boot 4.1.1 / Java 21 REST API, package root `edu.final_year.backendwebm`.

- **`user/`** — `User` JPA entity (`users` table: auto-increment `id`, unique `email`, `password` [BCrypt hash], `createdAt`) and `UserRepository`.
- **`auth/`** — authentication feature, JWT-based (stateless, no server-side session), with a refresh-token flow for renewing access tokens. Organized into sub-packages by role within the feature:
  - `AuthController`/`AuthService` (feature root) — `AuthController` exposes `POST /api/auth/signup`, `/login`, `/refresh`, `/logout`. `AuthService` handles signup (checks email uniqueness → `EmailAlreadyExistsException` → 409, hashes the password) and `logout` (see below), both `@Transactional`.
  - `auth/jwt/` — `JwtService` issues signed **access tokens** (JWT, HS384, `sub`=email, `jti`=random UUID, `exp` per `app.jwt.expiration-minutes`); `AppUserDetailsService` implements Spring Security's `UserDetailsService`, loading users by email; `JwtAuthenticationFilter` (a `OncePerRequestFilter` registered before `UsernamePasswordAuthenticationFilter`) parses/validates the bearer access token on every request, checks it isn't revoked, and populates the `SecurityContext` for that request only (nothing is persisted server-side per request).
  - `auth/token/` — `RefreshTokenService`/`RefreshToken`/`RefreshTokenRepository` and `RevokedToken`/`RevokedTokenRepository`. Refresh tokens are *not* JWTs — they're random 256-bit opaque strings (`RefreshTokenService.generateRawToken`). Only a SHA-256 hash of the token is persisted (`refresh_tokens` table, FK to `users`), so a DB read alone can't be used to authenticate. `POST /api/auth/refresh` looks up the hash, and if it's unexpired, **rotates** it: the old row is deleted and a new refresh token is issued alongside a fresh access token (`RefreshTokenService.rotate`). Presenting an already-rotated (or unknown/expired) refresh token throws `InvalidRefreshTokenException` → 401 — this also means refresh tokens are single-use.
  - `auth/dto/` — request/response records (`SignupRequest`, `LoginRequest`, `RefreshRequest`, `UserResponse`, `LoginResponse`) with Bean Validation annotations (`@Email`, `@NotBlank`, `@Size(min = 8)` on signup password).
  - Login authenticates via Spring Security's `AuthenticationManager`/`DaoAuthenticationProvider` (wired automatically from the `AppUserDetailsService` + `PasswordEncoder` beans), then issues both tokens. Both come back in the login/refresh response body (`accessToken`, `refreshToken`); the caller sends the access token back as `Authorization: Bearer <token>` and stores the refresh token to call `/api/auth/refresh` once the access token expires.
  - Logout (`AuthService.logout`) revokes the presented access token by inserting its `jti`/`exp` into `revoked_tokens` (checked by `JwtAuthenticationFilter`), **and** deletes all of that user's refresh tokens (`RefreshTokenService.revokeAllForUser`) so they can't be used to mint new access tokens either. Expired `revoked_tokens`/`refresh_tokens` rows are opportunistically deleted on logout/refresh calls — there's no scheduled cleanup job.
- **`config/SecurityConfig`** — the `SecurityFilterChain`: CSRF disabled, session policy `STATELESS`, `/api/auth/signup`, `/api/auth/login`, `/api/auth/refresh` permitted, everything else (including `/api/auth/logout`) requires a valid, non-revoked bearer access token. Defines the `PasswordEncoder` (BCrypt) and `AuthenticationManager` beans.
- **`exception/GlobalExceptionHandler`** — `@RestControllerAdvice` translating `MethodArgumentNotValidException` → 400 with field errors, `EmailAlreadyExistsException` → 409, `BadCredentialsException` → 401 (also covers unknown-email login attempts, since Spring Security's `DaoAuthenticationProvider` hides `UsernameNotFoundException` behind `BadCredentialsException` by default), `InvalidRefreshTokenException` → 401. Responses use the `ErrorResponse` record.

Any new authenticated endpoint outside `/api/auth/signup`, `/api/auth/login` and `/api/auth/refresh` is protected by default per `SecurityConfig`; the caller must send a valid, non-revoked `Authorization: Bearer <access token>` (obtained from `/api/auth/login` or `/api/auth/refresh`) or the request is rejected with 403.

Derived Spring Data delete queries that return `void` (e.g. `deleteAllByUser`, `deleteAllByExpiresAtBefore`) execute as per-entity `EntityManager.remove()` calls, not a bulk `DELETE`, so they require an active transaction — always call them from a `@Transactional` service method, not directly from a controller.
