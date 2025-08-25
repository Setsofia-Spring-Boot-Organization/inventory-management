package com.backend.inventory_management.inventory.procurement.purchaseorderline;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineRequest;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineResponse;

import java.util.List;

public interface PurchaseOrderLineService {
    PurchaseOrderLineResponse create(PurchaseOrderLineRequest request);
    PurchaseOrderLineResponse update(Long id, PurchaseOrderLineRequest request);
    void delete(Long id);
    PurchaseOrderLineResponse getById(Long id);
    List<PurchaseOrderLineResponse> getByPurchaseOrder(Long purchaseOrderId);
}
