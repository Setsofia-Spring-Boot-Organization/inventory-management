package com.backend.inventory_management.inventory.procurement.supplier;

import com.backend.inventory_management.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<BaseResponse<SupplierEntity>> createSupplier(@RequestBody SupplierEntity supplier) {
        return ResponseEntity.status(201).body(supplierService.createSupplier(supplier));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierEntity>> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<SupplierEntity>>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierEntity>> updateSupplier(@PathVariable Long id, @RequestBody SupplierEntity supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.deleteSupplier(id));
    }

    @GetMapping("/exists/{supplierCode}")
    public ResponseEntity<BaseResponse<Boolean>> existsBySupplierCode(@PathVariable String supplierCode) {
        return ResponseEntity.ok(supplierService.existsBySupplierCode(supplierCode));
    }
}
