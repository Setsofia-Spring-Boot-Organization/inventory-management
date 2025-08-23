package com.backend.inventory_management.core.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(Long id, UserEntity user);
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAll();
    Page<UserEntity> findAllWithPagination(Pageable pageable);
    Page<UserEntity> searchUsers(String search, Pageable pageable);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserEntity> findByStore(Long storeId);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void resetPassword(Long userId, String newPassword);
    void toggleUserStatus(Long userId);
}
