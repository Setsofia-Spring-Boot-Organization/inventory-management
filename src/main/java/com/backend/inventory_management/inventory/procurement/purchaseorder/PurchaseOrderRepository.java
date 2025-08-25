package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.inventory.procurement.supplier.SupplierEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {

    // Find by supplier entity
    Page<PurchaseOrderEntity> findBySupplier(SupplierEntity supplier, Pageable pageable);

    // Find by supplier ID
    Page<PurchaseOrderEntity> findBySupplier_Id(Long supplierId, Pageable pageable);

    // Find by store entity
    Page<PurchaseOrderEntity> findByStore(StoreEntity store, Pageable pageable);

    // Find by store ID
    Page<PurchaseOrderEntity> findByStore_Id(Long storeId, Pageable pageable);

    // Find by order number
    Optional<PurchaseOrderEntity> findByOrderNumber(String orderNumber);

    // Find by status with pagination
    Page<PurchaseOrderEntity> findByStatus(PurchaseOrderEntity.OrderStatus status, Pageable pageable);

    // Find by status without pagination
    List<PurchaseOrderEntity> findByStatus(PurchaseOrderEntity.OrderStatus status);

    // Find by date range using order date
    Page<PurchaseOrderEntity> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Find by created date range
    List<PurchaseOrderEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find by supplier and status
    List<PurchaseOrderEntity> findBySupplier_IdAndStatus(Long supplierId, PurchaseOrderEntity.OrderStatus status);

    // Find by store and status
    List<PurchaseOrderEntity> findByStore_IdAndStatus(Long storeId, PurchaseOrderEntity.OrderStatus status);

    // Custom queries for advanced filtering
    @Query("SELECT po FROM PurchaseOrderEntity po WHERE po.supplier.id = :supplierId AND po.status = :status")
    Page<PurchaseOrderEntity> findBySupplierAndStatus(@Param("supplierId") Long supplierId, @Param("status") PurchaseOrderEntity.OrderStatus status, Pageable pageable);

    @Query("SELECT po FROM PurchaseOrderEntity po WHERE po.totalAmount BETWEEN :minAmount AND :maxAmount")
    Page<PurchaseOrderEntity> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount, Pageable pageable);
}