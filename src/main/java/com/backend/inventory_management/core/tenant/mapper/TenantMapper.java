package com.backend.inventory_management.core.tenant.mapper;

import com.backend.inventory_management.core.tenant.TenantEntity;
import com.backend.inventory_management.core.tenant.dtos.TenantCreateRequestDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantResponseDTO;
import com.backend.inventory_management.core.tenant.dtos.TenantUpdateRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantEntity toEntity(TenantCreateRequestDTO dto) {
        TenantEntity entity = new TenantEntity();
        entity.setTenantCode(dto.getTenantCode());
        entity.setTenantName(dto.getTenantName());
        entity.setBusinessRegistrationNumber(dto.getBusinessRegistrationNumber());
        entity.setTaxNumber(dto.getTaxNumber());
        entity.setContactPerson(dto.getContactPerson());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactPhone(dto.getContactPhone());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setPostalCode(dto.getPostalCode());
        entity.setContractStartDate(dto.getContractStartDate());
        entity.setContractEndDate(dto.getContractEndDate());
        entity.setBusinessType(dto.getBusinessType());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public void updateEntityFromDTO(TenantUpdateRequestDTO dto, TenantEntity entity) {
        if (dto.getTenantName() != null) entity.setTenantName(dto.getTenantName());
        if (dto.getBusinessRegistrationNumber() != null) entity.setBusinessRegistrationNumber(dto.getBusinessRegistrationNumber());
        if (dto.getTaxNumber() != null) entity.setTaxNumber(dto.getTaxNumber());
        if (dto.getContactPerson() != null) entity.setContactPerson(dto.getContactPerson());
        if (dto.getContactEmail() != null) entity.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) entity.setContactPhone(dto.getContactPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getCity() != null) entity.setCity(dto.getCity());
        if (dto.getState() != null) entity.setState(dto.getState());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getPostalCode() != null) entity.setPostalCode(dto.getPostalCode());
        if (dto.getStatus() != null) entity.setStatus(TenantEntity.TenantStatus.valueOf(dto.getStatus().name()));
        if (dto.getContractStartDate() != null) entity.setContractStartDate(dto.getContractStartDate());
        if (dto.getContractEndDate() != null) entity.setContractEndDate(dto.getContractEndDate());
        if (dto.getBusinessType() != null) entity.setBusinessType(dto.getBusinessType());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
    }

    public TenantResponseDTO toResponseDTO(TenantEntity entity) {
        TenantResponseDTO dto = new TenantResponseDTO();
        dto.setId(entity.getId());
        dto.setTenantCode(entity.getTenantCode());
        dto.setTenantName(entity.getTenantName());
        dto.setBusinessRegistrationNumber(entity.getBusinessRegistrationNumber());
        dto.setTaxNumber(entity.getTaxNumber());
        dto.setContactPerson(entity.getContactPerson());
        dto.setContactEmail(entity.getContactEmail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setPostalCode(entity.getPostalCode());
        dto.setStatus(entity.getStatus().name());
        dto.setContractStartDate(entity.getContractStartDate());
        dto.setContractEndDate(entity.getContractEndDate());
        dto.setBusinessType(entity.getBusinessType());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
