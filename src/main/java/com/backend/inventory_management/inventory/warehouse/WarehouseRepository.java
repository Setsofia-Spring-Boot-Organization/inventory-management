package com.backend.inventory_management.inventory.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {

    Optional<WarehouseEntity> findByWarehouseCode(String warehouseCode);
    List<WarehouseEntity> findByStoreId(Long storeId);
    List<WarehouseEntity> findByType(WarehouseEntity.WarehouseType type);
    List<WarehouseEntity> findByStatus(WarehouseEntity.WarehouseStatus status);

    @Query("SELECT w FROM WarehouseEntity w WHERE w.store.id = ?1 AND w.status = 'ACTIVE'")
    List<WarehouseEntity> findActiveWarehousesByStore(Long storeId);

    boolean existsByWarehouseCode(String warehouseCode);
}