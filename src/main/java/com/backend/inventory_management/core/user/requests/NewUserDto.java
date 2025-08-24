package com.backend.inventory_management.core.user.requests;

import com.backend.inventory_management.common.constants.Constants;

/**
 * DTO representation of UserEntity.
 * This record is immutable and used for transferring user data
 * without exposing the entity directly.
 */
public record NewUserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String password,
        Constants.Role role,
        Long storeId,
        Boolean isActive,
        Boolean enabled
) {}
