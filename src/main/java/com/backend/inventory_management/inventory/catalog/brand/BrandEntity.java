package com.backend.inventory_management.inventory.catalog.brand;

import com.backend.inventory_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "brands")
public class BrandEntity extends BaseEntity {

    @NotBlank
    @Column(unique = true)
    private String brandCode;
    
    @NotBlank
    private String brandName;
    
    private String description;
    private String logoUrl;
    private String website;
    private String contactInfo;
    
    @Enumerated(EnumType.STRING)
    private BrandStatus status = BrandStatus.ACTIVE;
    
    public enum BrandStatus {
        ACTIVE, INACTIVE
    }

    // Constructors
    public BrandEntity() {}

    public BrandEntity(String brandCode, String brandName) {
        this.brandCode = brandCode;
        this.brandName = brandName;
    }

}