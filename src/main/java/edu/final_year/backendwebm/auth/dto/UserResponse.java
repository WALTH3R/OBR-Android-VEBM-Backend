package edu.final_year.backendwebm.auth.dto;

import edu.final_year.backendwebm.user.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String email, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}
