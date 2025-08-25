package com.backend.inventory_management.inventory.procurement.purchaseorderline;

import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineRequest;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-order-lines")
public class PurchaseOrderLineController {

    @Autowired
    private PurchaseOrderLineService service;

    @PostMapping
    public ResponseEntity<BaseResponse<PurchaseOrderLineResponse>> create(@Valid @RequestBody PurchaseOrderLineRequest request) {
        return ResponseEntity.ok(BaseResponse.success(service.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderLineResponse>> update(@PathVariable Long id, @Valid @RequestBody PurchaseOrderLineRequest request) {
        return ResponseEntity.ok(BaseResponse.success(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderLineResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(service.getById(id)));
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    public ResponseEntity<BaseResponse<List<PurchaseOrderLineResponse>>> getByPurchaseOrder(@PathVariable Long purchaseOrderId) {
        return ResponseEntity.ok(BaseResponse.success(service.getByPurchaseOrder(purchaseOrderId)));
    }
}
