package com.backend.inventory_management.core.tenant;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.core.tenant.dtos.TenantCreateRequestDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantResponseDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantUpdateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_PREFIX + "/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<BaseResponse<TenantResponseDTO>> createTenant(@Valid @RequestBody TenantCreateRequestDTO dto) {
        TenantResponseDTO tenant = tenantService.createTenant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(tenant));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TenantResponseDTO>> updateTenant(@PathVariable Long id, @RequestBody TenantUpdateRequestDTO dto) {
        TenantResponseDTO updatedTenant = tenantService.updateTenant(id, dto);
        return ResponseEntity.ok(BaseResponse.success(updatedTenant));
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TenantResponseDTO>> getTenantById(@PathVariable Long id) {
        TenantResponseDTO tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(BaseResponse.success(tenant));
    }


    @GetMapping("/code/{tenantCode}")
    public ResponseEntity<BaseResponse<TenantResponseDTO>> getTenantByCode(@PathVariable String tenantCode) {
        TenantResponseDTO tenant = tenantService.getTenantByCode(tenantCode);
        return ResponseEntity.ok(BaseResponse.success(tenant));
    }


    @GetMapping
    public ResponseEntity<BaseResponse<Page<TenantResponseDTO>>> getAllTenants(Pageable pageable) {
        Page<TenantResponseDTO> tenants = tenantService.getAllTenants(pageable);
        return ResponseEntity.ok(BaseResponse.success(tenants));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Tenant deleted successfully"));
    }
}
