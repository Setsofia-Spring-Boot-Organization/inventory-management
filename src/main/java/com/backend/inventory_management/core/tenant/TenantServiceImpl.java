package com.backend.inventory_management.core.tenant;

import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import com.backend.inventory_management.common.exception.DuplicateResourceException;
import com.backend.inventory_management.core.tenant.dtos.TenantCreateRequestDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantResponseDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantUpdateRequestDTO;
import com.backend.inventory_management.core.tenant.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponseDTO createTenant(TenantCreateRequestDTO createRequest) {
        if (tenantRepository.existsByTenantCode(createRequest.getTenantCode())) {
            throw new DuplicateResourceException("Tenant with code " + createRequest.getTenantCode() + " already exists");
        }

        TenantEntity tenant = tenantMapper.toEntity(createRequest);
        TenantEntity saved = tenantRepository.save(tenant);
        return tenantMapper.toResponseDTO(saved);
    }

    @Override
    public TenantResponseDTO updateTenant(Long id, TenantUpdateRequestDTO updateRequest) {
        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));

        tenantMapper.updateEntityFromDTO(updateRequest, tenant);
        TenantEntity updated = tenantRepository.save(tenant);
        return tenantMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponseDTO getTenantById(Long id) {
        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));
        return tenantMapper.toResponseDTO(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponseDTO getTenantByCode(String tenantCode) {
        TenantEntity tenant = tenantRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with code " + tenantCode));
        return tenantMapper.toResponseDTO(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantResponseDTO> getAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable)
                .map(tenantMapper::toResponseDTO);
    }

    @Override
    public void deleteTenant(Long id) {
        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));
        tenantRepository.delete(tenant);
    }
}
