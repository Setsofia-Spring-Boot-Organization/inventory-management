package com.backend.inventory_management.inventory.warehouse;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.store.StoreEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "warehouses")
public class WarehouseEntity extends BaseEntity {

    // Getters and setters
    @NotBlank
    @Column(unique = true)
    private String warehouseCode;
    
    @NotBlank
    private String warehouseName;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @NotBlank
    private StoreEntity store;
    
    @Enumerated(EnumType.STRING)
    private WarehouseType type = WarehouseType.STORAGE;
    
    private String location;
    private Double capacity;
    private String temperatureRange;
    
    @Enumerated(EnumType.STRING)
    private WarehouseStatus status = WarehouseStatus.ACTIVE;
    
    public enum WarehouseType {
        STORAGE, DISPLAY_FLOOR, COLD_STORAGE, PHARMACY, ELECTRONICS, RETURNS
    }
    
    public enum WarehouseStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }

    // Constructors
    public WarehouseEntity() {}

    public WarehouseEntity(String warehouseCode, String warehouseName, StoreEntity store) {
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
        this.store = store;
    }

}