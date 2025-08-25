package com.backend.inventory_management.inventory.procurement.supplier;

import com.backend.inventory_management.common.dto.BaseResponse;

import java.util.List;

public interface SupplierService {
    BaseResponse<SupplierEntity> createSupplier(SupplierEntity supplier);
    BaseResponse<SupplierEntity> getSupplierById(Long id);
    BaseResponse<List<SupplierEntity>> getAllSuppliers();
    BaseResponse<SupplierEntity> updateSupplier(Long id, SupplierEntity supplier);
    BaseResponse<Void> deleteSupplier(Long id);
    BaseResponse<Boolean> existsBySupplierCode(String supplierCode);
}
