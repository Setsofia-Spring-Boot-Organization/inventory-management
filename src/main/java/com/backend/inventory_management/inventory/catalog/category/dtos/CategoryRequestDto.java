package com.backend.inventory_management.inventory.catalog.category.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
    @NotBlank
    private String categoryCode;

    @NotBlank
    private String categoryName;

    private String description;
    private Long parentCategoryId;
    private String imageUrl;
    private Integer sortOrder;
}
