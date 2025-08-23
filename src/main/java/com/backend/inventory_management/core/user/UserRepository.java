package com.backend.inventory_management.core.user;

import com.backend.inventory_management.common.constants.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByRole(Constants.Role role);
    List<UserEntity> findByStoreId(Long storeId);

    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true AND u.isActive = true")
    List<UserEntity> findActiveUsers();

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserEntity> findUsersWithSearch(@Param("search") String search, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}