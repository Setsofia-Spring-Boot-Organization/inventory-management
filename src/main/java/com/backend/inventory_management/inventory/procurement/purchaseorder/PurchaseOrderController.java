package com.backend.inventory_management.inventory.procurement.purchaseorder;

import com.backend.inventory_management.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<BaseResponse<PurchaseOrderEntity>> create(@RequestBody PurchaseOrderEntity purchaseOrder) {
        return ResponseEntity.status(201).body(purchaseOrderService.createPurchaseOrder(purchaseOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderEntity>> update(@PathVariable Long id, @RequestBody PurchaseOrderEntity purchaseOrder) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(id, purchaseOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.deletePurchaseOrder(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderEntity>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<PurchaseOrderEntity>>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<BaseResponse<Page<PurchaseOrderEntity>>> getBySupplier(@PathVariable Long supplierId, Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersBySupplier(supplierId, pageable));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<BaseResponse<Page<PurchaseOrderEntity>>> getByStore(@PathVariable Long storeId, Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByStore(storeId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<Page<PurchaseOrderEntity>>> getByStatus(@PathVariable PurchaseOrderEntity.OrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByStatus(status, pageable));
    }

    @GetMapping("/date-range")
    public ResponseEntity<BaseResponse<Page<PurchaseOrderEntity>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByDateRange(startDate, endDate, pageable));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<PurchaseOrderEntity>> approve(@PathVariable Long id, @RequestParam Long approverId) {
        return ResponseEntity.ok(purchaseOrderService.approvePurchaseOrder(id, approverId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BaseResponse<PurchaseOrderEntity>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.cancelPurchaseOrder(id));
    }
}
