package com.backend.inventory_management.inventory.movement;

import com.backend.inventory_management.common.dto.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementService {

    BaseResponse<StockMovementEntity> createStockMovement(StockMovementEntity stockMovement);

    BaseResponse<StockMovementEntity> getStockMovementById(Long id);

    BaseResponse<List<StockMovementEntity>> getAllStockMovements();

    BaseResponse<List<StockMovementEntity>> getByProductId(Long productId);

    BaseResponse<List<StockMovementEntity>> getByWarehouseId(Long warehouseId);

    BaseResponse<List<StockMovementEntity>> getByMovementType(StockMovementEntity.MovementType movementType);

    BaseResponse<List<StockMovementEntity>> getByReferenceNumber(String referenceNumber);

    BaseResponse<List<StockMovementEntity>> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    BaseResponse<Page<StockMovementEntity>> getProductMovementHistory(Long productId,
                                                                     LocalDateTime startDate,
                                                                     LocalDateTime endDate,
                                                                     Pageable pageable);

    BaseResponse<Void> deleteStockMovement(Long id);
}
