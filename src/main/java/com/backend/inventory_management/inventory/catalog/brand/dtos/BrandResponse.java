package com.backend.inventory_management.inventory.catalog.brand.dtos;

import com.backend.inventory_management.inventory.catalog.brand.BrandEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BrandResponse {
    private Long id;
    private String brandCode;
    private String brandName;
    private String description;
    private String logoUrl;
    private String website;
    private String contactInfo;
    private BrandEntity.BrandStatus status;
}
