package com.backend.inventory_management.features.auth.daos;

import com.backend.inventory_management.features.auth.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private UserDto user;
}