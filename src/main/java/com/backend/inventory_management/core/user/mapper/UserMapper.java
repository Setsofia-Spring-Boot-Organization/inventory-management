package com.backend.inventory_management.core.user.mapper;

import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.core.user.requests.UserResponseDto;

public class UserMapper {

    public static UserResponseDto toDto(UserEntity user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
