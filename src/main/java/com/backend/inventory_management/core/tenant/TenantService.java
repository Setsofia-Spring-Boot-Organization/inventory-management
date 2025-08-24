package com.backend.inventory_management.core.tenant;

import com.backend.inventory_management.core.tenant.dtos.TenantCreateRequestDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantResponseDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantService {
    TenantResponseDTO createTenant(TenantCreateRequestDTO createRequest);
    TenantResponseDTO updateTenant(Long id, TenantUpdateRequestDTO updateRequest);
    TenantResponseDTO getTenantById(Long id);
    TenantResponseDTO getTenantByCode(String tenantCode);
    Page<TenantResponseDTO> getAllTenants(Pageable pageable);
    void deleteTenant(Long id);
}
