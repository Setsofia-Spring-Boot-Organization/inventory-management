package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.core.store.StoreRepository;
import com.backend.inventory_management.core.user.UserEntity;
import com.backend.inventory_management.core.user.UserRepository;
import com.backend.inventory_management.inventory.procurement.supplier.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public BaseResponse<PurchaseOrderEntity> createPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        try {
            // Validation
            if (purchaseOrder.getOrderLines() == null || purchaseOrder.getOrderLines().isEmpty()) {
                return BaseResponse.error("Purchase order must contain at least one order line");
            }

            // Generate order number if not provided
            if (purchaseOrder.getOrderNumber() == null || purchaseOrder.getOrderNumber().trim().isEmpty()) {
                purchaseOrder.setOrderNumber(generateOrderNumber());
            }

            // Set default order date if not provided
            if (purchaseOrder.getOrderDate() == null) {
                purchaseOrder.setOrderDate(LocalDate.now());
            }

            // Ensure status is set
            if (purchaseOrder.getStatus() == null) {
                purchaseOrder.setStatus(PurchaseOrderEntity.OrderStatus.DRAFT);
            }

            // Calculate totals
            purchaseOrder.calculateTotals();

            // Save order lines with proper reference
            purchaseOrder.getOrderLines().forEach(line -> line.setPurchaseOrder(purchaseOrder));

            PurchaseOrderEntity saved = purchaseOrderRepository.save(purchaseOrder);
            log.info("Created purchase order with ID: {}", saved.getId());

            return BaseResponse.success("Purchase order created successfully", saved);
        } catch (Exception e) {
            log.error("Error creating purchase order", e);
            return BaseResponse.error("Failed to create purchase order: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PurchaseOrderEntity> updatePurchaseOrder(Long id, PurchaseOrderEntity purchaseOrder) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(existing -> {
                        // Check if order can be updated
                        if (existing.getStatus() == PurchaseOrderEntity.OrderStatus.CANCELLED ||
                                existing.getStatus() == PurchaseOrderEntity.OrderStatus.CLOSED) {
                            return BaseResponse.<PurchaseOrderEntity>error("Cannot update cancelled or closed purchase orders");
                        }

                        // Update allowed fields
                        if (purchaseOrder.getSupplier() != null) {
                            existing.setSupplier(purchaseOrder.getSupplier());
                        }
                        if (purchaseOrder.getStore() != null) {
                            existing.setStore(purchaseOrder.getStore());
                        }
                        if (purchaseOrder.getOrderDate() != null) {
                            existing.setOrderDate(purchaseOrder.getOrderDate());
                        }
                        if (purchaseOrder.getExpectedDeliveryDate() != null) {
                            existing.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
                        }
                        if (purchaseOrder.getPriority() != null) {
                            existing.setPriority(purchaseOrder.getPriority());
                        }

                        existing.setPaymentTerms(purchaseOrder.getPaymentTerms());
                        existing.setDeliveryAddress(purchaseOrder.getDeliveryAddress());
                        existing.setNotes(purchaseOrder.getNotes());
                        existing.setTerms(purchaseOrder.getTerms());

                        // Update order lines if provided
                        if (purchaseOrder.getOrderLines() != null) {
                            existing.getOrderLines().clear();
                            purchaseOrder.getOrderLines().forEach(line -> {
                                line.setPurchaseOrder(existing);
                                existing.getOrderLines().add(line);
                            });
                        }

                        existing.calculateTotals();
                        PurchaseOrderEntity updated = purchaseOrderRepository.save(existing);

                        log.info("Updated purchase order with ID: {}", updated.getId());
                        return BaseResponse.success("Purchase order updated successfully", updated);
                    }).orElse(BaseResponse.error("Purchase order not found"));
        } catch (Exception e) {
            log.error("Error updating purchase order with ID: {}", id, e);
            return BaseResponse.error("Failed to update purchase order: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> deletePurchaseOrder(Long id) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(po -> {
                        // Only allow deletion of draft orders
                        if (po.getStatus() != PurchaseOrderEntity.OrderStatus.DRAFT) {
                            return BaseResponse.<Void>error("Only draft purchase orders can be deleted");
                        }

                        purchaseOrderRepository.delete(po);
                        log.info("Deleted purchase order with ID: {}", id);
                        return BaseResponse.<Void>success("Purchase order deleted successfully", null);
                    }).orElse(BaseResponse.error("Purchase order not found"));
        } catch (Exception e) {
            log.error("Error deleting purchase order with ID: {}", id, e);
            return BaseResponse.error("Failed to delete purchase order: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<PurchaseOrderEntity> getPurchaseOrderById(Long id) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(po -> BaseResponse.success("Purchase order retrieved successfully", po))
                    .orElse(BaseResponse.error("Purchase order not found"));
        } catch (Exception e) {
            log.error("Error retrieving purchase order with ID: {}", id, e);
            return BaseResponse.error("Failed to retrieve purchase order: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<PurchaseOrderEntity>> getAllPurchaseOrders() {
        try {
            List<PurchaseOrderEntity> orders = purchaseOrderRepository.findAll();
            return BaseResponse.success("Purchase orders retrieved successfully", orders);
        } catch (Exception e) {
            log.error("Error retrieving all purchase orders", e);
            return BaseResponse.error("Failed to retrieve purchase orders: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersBySupplier(Long supplierId, Pageable pageable) {
        try {
            if (!supplierRepository.existsById(supplierId)) {
                return BaseResponse.error("Supplier not found");
            }

            Page<PurchaseOrderEntity> orders = purchaseOrderRepository.findBySupplier_Id(supplierId, pageable);
            return BaseResponse.success("Purchase orders retrieved successfully", orders);
        } catch (Exception e) {
            log.error("Error retrieving purchase orders for supplier ID: {}", supplierId, e);
            return BaseResponse.error("Failed to retrieve purchase orders: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByStore(Long storeId, Pageable pageable) {
        try {
            if (!storeRepository.existsById(storeId)) {
                return BaseResponse.error("Store not found");
            }

            Page<PurchaseOrderEntity> orders = purchaseOrderRepository.findByStore_Id(storeId, pageable);
            return BaseResponse.success("Purchase orders retrieved successfully", orders);
        } catch (Exception e) {
            log.error("Error retrieving purchase orders for store ID: {}", storeId, e);
            return BaseResponse.error("Failed to retrieve purchase orders: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByStatus(PurchaseOrderEntity.OrderStatus status, Pageable pageable) {
        try {
            Page<PurchaseOrderEntity> orders = purchaseOrderRepository.findByStatus(status, pageable);
            return BaseResponse.success("Purchase orders retrieved successfully", orders);
        } catch (Exception e) {
            log.error("Error retrieving purchase orders by status: {}", status, e);
            return BaseResponse.error("Failed to retrieve purchase orders: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<Page<PurchaseOrderEntity>> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            if (startDate.isAfter(endDate)) {
                return BaseResponse.error("Start date must be before or equal to end date");
            }

            Page<PurchaseOrderEntity> orders = purchaseOrderRepository.findByOrderDateBetween(startDate, endDate, pageable);
            return BaseResponse.success("Purchase orders retrieved successfully", orders);
        } catch (Exception e) {
            log.error("Error retrieving purchase orders for date range: {} to {}", startDate, endDate, e);
            return BaseResponse.error("Failed to retrieve purchase orders: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PurchaseOrderEntity> approvePurchaseOrder(Long id, Long approverId) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(po -> {
                        // Check if order can be approved
                        if (po.getStatus() != PurchaseOrderEntity.OrderStatus.PENDING &&
                                po.getStatus() != PurchaseOrderEntity.OrderStatus.SUBMITTED) {
                            return BaseResponse.<PurchaseOrderEntity>error("Only pending or submitted purchase orders can be approved");
                        }

                        // Validate approver
                        UserEntity approver = userRepository.findById(approverId)
                                .orElse(null);
                        if (approver == null) {
                            return BaseResponse.<PurchaseOrderEntity>error("Approver user not found");
                        }

                        po.setApprovedByUser(approver);
                        po.setStatus(PurchaseOrderEntity.OrderStatus.APPROVED);

                        PurchaseOrderEntity updated = purchaseOrderRepository.save(po);
                        log.info("Approved purchase order with ID: {} by user ID: {}", id, approverId);

                        return BaseResponse.success("Purchase order approved successfully", updated);
                    }).orElse(BaseResponse.error("Purchase order not found"));
        } catch (Exception e) {
            log.error("Error approving purchase order with ID: {}", id, e);
            return BaseResponse.error("Failed to approve purchase order: " + e.getMessage());
        }
    }


    @Override
    public BaseResponse<PurchaseOrderEntity> cancelPurchaseOrder(Long id) {
        try {
            return purchaseOrderRepository.findById(id)
                    .map(po -> {
                        // Check if order can be cancelled
                        if (po.getStatus() == PurchaseOrderEntity.OrderStatus.CANCELLED) {
                            return BaseResponse.<PurchaseOrderEntity>error("Purchase order is already cancelled");
                        }
                        if (po.getStatus() == PurchaseOrderEntity.OrderStatus.FULLY_RECEIVED ||
                                po.getStatus() == PurchaseOrderEntity.OrderStatus.CLOSED) {
                            return BaseResponse.<PurchaseOrderEntity>error("Cannot cancel received or closed purchase orders");
                        }

                        po.setStatus(PurchaseOrderEntity.OrderStatus.CANCELLED);
                        PurchaseOrderEntity updated = purchaseOrderRepository.save(po);
                        log.info("Cancelled purchase order with ID: {}", id);

                        return BaseResponse.success("Purchase order cancelled successfully", updated);
                    }).orElse(BaseResponse.error("Purchase order not found"));
        } catch (Exception e) {
            log.error("Error cancelling purchase order with ID: {}", id, e);
            return BaseResponse.error("Failed to cancel purchase order: " + e.getMessage());
        }
    }


    private String generateOrderNumber() {
        return "PO-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}