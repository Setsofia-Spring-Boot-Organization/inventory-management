package com.backend.inventory_management.core.store.dtos;

import com.backend.inventory_management.core.store.StoreEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoreResponseDTO {

    private Long id;
    private String storeCode;
    private String storeName;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String managerName;
    private StoreEntity.StoreType storeType;
    private StoreEntity.StoreStatus status;
    private Double floorArea;
    private Integer numberOfFloors;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}