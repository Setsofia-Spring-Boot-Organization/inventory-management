package com.backend.inventory_management.core.user.requests;

import lombok.Builder;

/**
 * This DTO represents a simplified user response object returned to clients.
 * It avoids exposing the full UserEntity and prevents serialization issues
 * with Hibernate lazy-loading proxies.
 */
@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role
) {}
