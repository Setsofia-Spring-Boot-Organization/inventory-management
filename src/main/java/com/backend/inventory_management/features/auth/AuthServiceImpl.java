// AuthServiceImpl.java
package com.backend.inventory_management.features.auth;

import com.backend.inventory_management.configs.JwtTokenProvider;
import com.backend.inventory_management.features.auth.daos.LoginResponseDto;
import com.backend.inventory_management.features.auth.dtos.LoginRequestDto;
import com.backend.inventory_management.features.auth.dtos.RegisterRequestDto;
import com.backend.inventory_management.features.auth.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Update last login
            userRepository.updateLastLogin(user.getId(), LocalDateTime.now());
            
            UserDto userDto = convertToUserDto(user);
            userDto.setLastLogin(LocalDateTime.now());
            
            return LoginResponseDto.builder()
                .token(token)
                .user(userDto)
                .build();
                
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @Override
    @Transactional
    public UserDto register(RegisterRequestDto registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
            .username(registerRequest.getUsername())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .email(registerRequest.getEmail())
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .role(registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER)
            .enabled(true)
            .build();
        
        User savedUser = userRepository.save(user);
        return convertToUserDto(savedUser);
    }
    
    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToUserDto(user);
    }
    
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
    
    @Override
    public String generateToken(User user) {
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        return jwtTokenProvider.generateToken(authToken);
    }
    
    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .lastLogin(user.getLastLogin())
            .build();
    }
}