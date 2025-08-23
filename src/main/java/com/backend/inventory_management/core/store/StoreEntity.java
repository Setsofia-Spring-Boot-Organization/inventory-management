package com.backend.inventory_management.core.store;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.core.tenant.TenantEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stores")
@Data
public class StoreEntity extends BaseEntity {

    @NotBlank
    @Column(unique = true)
    private String storeCode;

    @NotBlank
    private String storeName;

    private String description;

    @NotBlank
    private String address;

    private String city;
    private String state;
    private String country;
    private String postalCode;

    private String phoneNumber;
    private String email;
    private String managerName;

    @Enumerated(EnumType.STRING)
    private StoreType storeType;

    @Enumerated(EnumType.STRING)
    private StoreStatus status = StoreStatus.ACTIVE;

    private Double floorArea;
    private Integer numberOfFloors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    @NotNull
    private TenantEntity tenant;

    public enum StoreType {
        SUPERMARKET, ELECTRONICS, CLOTHING, PHARMACY, RESTAURANT, SERVICES, OTHER
    }

    public enum StoreStatus {
        ACTIVE, INACTIVE, MAINTENANCE, CLOSED
    }

    // Constructors
    public StoreEntity() {}

    public StoreEntity(String storeCode, String storeName, StoreType storeType, TenantEntity tenant) {
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.storeType = storeType;
        this.tenant = tenant;
    }
}

