package com.backend.inventory_management.core.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    @Query("SELECT s FROM StoreEntity s WHERE s.isActive = true AND " +
           "(LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<StoreEntity> findBySearchTerm(String search);
}