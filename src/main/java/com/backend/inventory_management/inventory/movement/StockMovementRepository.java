package com.backend.inventory_management.inventory.movement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

    List<StockMovementEntity> findByProductId(Long productId);
    List<StockMovementEntity> findByWarehouseId(Long warehouseId);
    List<StockMovementEntity> findByMovementType(StockMovementEntity.MovementType movementType);
    List<StockMovementEntity> findByReferenceNumber(String referenceNumber);

    @Query("SELECT sm FROM StockMovementEntity sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovementEntity> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sm FROM StockMovementEntity sm WHERE sm.product.id = :productId AND " +
            "sm.createdAt BETWEEN :startDate AND :endDate ORDER BY sm.createdAt DESC")
    Page<StockMovementEntity> findProductMovementHistory(@Param("productId") Long productId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
