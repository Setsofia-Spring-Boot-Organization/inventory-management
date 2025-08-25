package com.backend.inventory_management.inventory.movement;

import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    @Override
    public BaseResponse<StockMovementEntity> createStockMovement(StockMovementEntity stockMovement) {
        StockMovementEntity saved = stockMovementRepository.save(stockMovement);
        return BaseResponse.success("Stock movement created successfully", saved);
    }

    @Override
    public BaseResponse<StockMovementEntity> getStockMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .map(sm -> BaseResponse.success("Stock movement retrieved successfully", sm))
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id " + id));
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getAllStockMovements() {
        List<StockMovementEntity> movements = stockMovementRepository.findAll();
        return BaseResponse.success("Stock movements retrieved successfully", movements);
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getByProductId(Long productId) {
        return BaseResponse.success("Stock movements by product retrieved successfully",
                stockMovementRepository.findByProductId(productId));
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getByWarehouseId(Long warehouseId) {
        return BaseResponse.success("Stock movements by warehouse retrieved successfully",
                stockMovementRepository.findByWarehouseId(warehouseId));
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getByMovementType(StockMovementEntity.MovementType movementType) {
        return BaseResponse.success("Stock movements by type retrieved successfully",
                stockMovementRepository.findByMovementType(movementType));
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getByReferenceNumber(String referenceNumber) {
        return BaseResponse.success("Stock movements by reference number retrieved successfully",
                stockMovementRepository.findByReferenceNumber(referenceNumber));
    }

    @Override
    public BaseResponse<List<StockMovementEntity>> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return BaseResponse.success("Stock movements by date range retrieved successfully",
                stockMovementRepository.findByDateRange(startDate, endDate));
    }

    @Override
    public BaseResponse<Page<StockMovementEntity>> getProductMovementHistory(Long productId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockMovementEntity> history = stockMovementRepository.findProductMovementHistory(productId, startDate, endDate, pageable);
        return BaseResponse.success("Product movement history retrieved successfully", history);
    }

    @Override
    public BaseResponse<Void> deleteStockMovement(Long id) {
        if (!stockMovementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stock movement not found with id " + id);
        }
        stockMovementRepository.deleteById(id);
        return BaseResponse.success("Stock movement deleted successfully", null);
    }
}
