// AuthController.java
package com.backend.inventory_management.features.auth;

import com.backend.inventory_management.core.Response;
import com.backend.inventory_management.features.auth.daos.LoginResponseDto;
import com.backend.inventory_management.features.auth.dtos.LoginRequestDto;
import com.backend.inventory_management.features.auth.dtos.RegisterRequestDto;
import com.backend.inventory_management.features.auth.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(Response.success(loginResponse, "Login successful"));
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error("Invalid credentials"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Response<UserDto>> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            UserDto user = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(user, "User registered successfully"));
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Response<UserDto>> getCurrentUser() {
        try {
            UserDto user = authService.getCurrentUser();
            return ResponseEntity.ok(Response.success(user));
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error("User not authenticated"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(Response.success(null, "Logout successful"));
        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Logout failed"));
        }
    }
}