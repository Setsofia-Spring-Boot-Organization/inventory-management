package com.backend.inventory_management.inventory.procurement.purchaseorderline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLineEntity, Long> {
    List<PurchaseOrderLineEntity> findByPurchaseOrderId(Long purchaseOrderId);
}
