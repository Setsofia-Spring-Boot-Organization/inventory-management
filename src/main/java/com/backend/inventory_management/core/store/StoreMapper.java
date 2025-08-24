package com.backend.inventory_management.core.store;

import com.backend.inventory_management.core.store.dtos.StoreCreateRequestDTO;
import com.backend.inventory_management.core.store.dtos.StoreResponseDTO;
import com.backend.inventory_management.core.store.dtos.StoreUpdateRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public StoreEntity toEntity(StoreCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        StoreEntity entity = new StoreEntity();
        entity.setStoreCode(dto.getStoreCode());
        entity.setStoreName(dto.getStoreName());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setPostalCode(dto.getPostalCode());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setEmail(dto.getEmail());
        entity.setManagerName(dto.getManagerName());
        entity.setStoreType(dto.getStoreType());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StoreEntity.StoreStatus.ACTIVE);
        entity.setFloorArea(dto.getFloorArea());
        entity.setNumberOfFloors(dto.getNumberOfFloors());

        return entity;
    }

    public StoreResponseDTO toResponseDTO(StoreEntity entity) {
        if (entity == null) {
            return null;
        }

        StoreResponseDTO dto = new StoreResponseDTO();
        dto.setId(entity.getId());
        dto.setStoreCode(entity.getStoreCode());
        dto.setStoreName(entity.getStoreName());
        dto.setDescription(entity.getDescription());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setPostalCode(entity.getPostalCode());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setManagerName(entity.getManagerName());
        dto.setStoreType(entity.getStoreType());
        dto.setStatus(entity.getStatus());
        dto.setFloorArea(entity.getFloorArea());
        dto.setNumberOfFloors(entity.getNumberOfFloors());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        // Set tenant information
        if (entity.getTenant() != null) {
            dto.setTenantId(entity.getTenant().getId());
            dto.setTenantName(entity.getTenant().getTenantName());
        }

        return dto;
    }

    public void updateEntityFromDTO(StoreUpdateRequestDTO dto, StoreEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getStoreName() != null) {
            entity.setStoreName(dto.getStoreName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            entity.setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            entity.setState(dto.getState());
        }
        if (dto.getCountry() != null) {
            entity.setCountry(dto.getCountry());
        }
        if (dto.getPostalCode() != null) {
            entity.setPostalCode(dto.getPostalCode());
        }
        if (dto.getPhoneNumber() != null) {
            entity.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getManagerName() != null) {
            entity.setManagerName(dto.getManagerName());
        }
        if (dto.getStoreType() != null) {
            entity.setStoreType(dto.getStoreType());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getFloorArea() != null) {
            entity.setFloorArea(dto.getFloorArea());
        }
        if (dto.getNumberOfFloors() != null) {
            entity.setNumberOfFloors(dto.getNumberOfFloors());
        }
    }
}