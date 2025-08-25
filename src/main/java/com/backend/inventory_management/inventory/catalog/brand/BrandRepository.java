package com.backend.inventory_management.inventory.catalog.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    Optional<BrandEntity> findByBrandCode(String brandCode);

    boolean existsByBrandCode(String brandCode);

    List<BrandEntity> findByStatus(BrandEntity.BrandStatus status);
}
