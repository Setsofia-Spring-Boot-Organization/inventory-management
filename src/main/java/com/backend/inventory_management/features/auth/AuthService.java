// AuthService.java
package com.backend.inventory_management.features.auth;

import com.backend.inventory_management.features.auth.daos.LoginResponseDto;
import com.backend.inventory_management.features.auth.dtos.LoginRequestDto;
import com.backend.inventory_management.features.auth.dtos.RegisterRequestDto;
import com.backend.inventory_management.features.auth.dtos.UserDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest);
    UserDto register(RegisterRequestDto registerRequest);
    UserDto getCurrentUser();
    void logout();
    boolean validateToken(String token);
    String generateToken(User user);
}