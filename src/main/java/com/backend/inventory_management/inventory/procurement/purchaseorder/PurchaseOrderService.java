package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.common.dto.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {

    BaseResponse<PurchaseOrderEntity> createPurchaseOrder(PurchaseOrderEntity purchaseOrder);

    BaseResponse<PurchaseOrderEntity> updatePurchaseOrder(Long id, PurchaseOrderEntity purchaseOrder);

    BaseResponse<Void> deletePurchaseOrder(Long id);

    BaseResponse<PurchaseOrderEntity> getPurchaseOrderById(Long id);

    BaseResponse<List<PurchaseOrderEntity>> getAllPurchaseOrders();

    BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersBySupplier(Long supplierId, Pageable pageable);

    BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByStore(Long storeId, Pageable pageable);

    BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByStatus(PurchaseOrderEntity.OrderStatus status, Pageable pageable);

    BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    BaseResponse<PurchaseOrderEntity> approvePurchaseOrder(Long id, Long approverId);

    BaseResponse<PurchaseOrderEntity> cancelPurchaseOrder(Long id);
}