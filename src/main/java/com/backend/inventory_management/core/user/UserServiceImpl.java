package com.backend.inventory_management.core.user;

import com.backend.inventory_management.common.exception.DuplicateResourceException;
import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import com.backend.inventory_management.core.user.requests.NewUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(NewUserDto newUserDto) {
        if (existsByUsername(newUserDto.username())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (existsByEmail(newUserDto.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserEntity user = new UserEntity();

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(newUserDto.password()));

        // Ensure default values are set
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity user) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check for duplicate username/email only if they're being changed
        if (!existingUser.getUsername().equals(user.getUsername()) && existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (!existingUser.getEmail().equals(user.getEmail()) && existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Update fields - don't update username and password in regular update
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRole(user.getRole());
        existingUser.setStore(user.getStore());

        // Only update status fields if they're provided
        if (user.getIsActive() != null) {
            existingUser.setIsActive(user.getIsActive());
        }
        if (user.getEnabled() != null) {
            existingUser.setEnabled(user.getEnabled());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<UserEntity> findAllWithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<UserEntity> searchUsers(String search, Pageable pageable) {
        return userRepository.findUsersWithSearch(search, pageable);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Soft delete by setting isActive to false
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserEntity> findByStore(Long storeId) {
        return userRepository.findByStoreId(storeId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Validate new password (you might want to add more validation)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Toggle the enabled status
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        userRepository.save(user);
    }
}