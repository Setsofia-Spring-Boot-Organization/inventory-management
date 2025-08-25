package com.backend.inventory_management.inventory.procurement.supplier;

import com.backend.inventory_management.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public BaseResponse<SupplierEntity> createSupplier(SupplierEntity supplier) {
        if (supplierRepository.existsBySupplierCode(supplier.getSupplierCode())) {
            return BaseResponse.error("Supplier code already exists");
        }
        return BaseResponse.success(supplierRepository.save(supplier));
    }

    @Override
    public BaseResponse<SupplierEntity> getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(BaseResponse::success)
                .orElse(BaseResponse.error("Supplier not found"));
    }

    @Override
    public BaseResponse<List<SupplierEntity>> getAllSuppliers() {
        return BaseResponse.success(supplierRepository.findAll());
    }

    @Override
    public BaseResponse<SupplierEntity> updateSupplier(Long id, SupplierEntity supplier) {
        return supplierRepository.findById(id)
                .map(existing -> {
                    supplier.setId(id);
                    return BaseResponse.success(supplierRepository.save(supplier));
                })
                .orElse(BaseResponse.error("Supplier not found"));
    }

    @Override
    public BaseResponse<Void> deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            return BaseResponse.error("Supplier not found");
        }
        supplierRepository.deleteById(id);
        return BaseResponse.success(null);
    }

    @Override
    public BaseResponse<Boolean> existsBySupplierCode(String supplierCode) {
        return BaseResponse.success(supplierRepository.existsBySupplierCode(supplierCode));
    }
}
