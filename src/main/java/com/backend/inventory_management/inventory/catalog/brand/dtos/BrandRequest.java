package com.backend.inventory_management.inventory.catalog.brand.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequest {
    
    @NotBlank
    private String brandCode;
    
    @NotBlank
    private String brandName;

    private String description;
    private String logoUrl;
    private String website;
    private String contactInfo;
}
