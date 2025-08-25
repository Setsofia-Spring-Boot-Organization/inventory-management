package com.backend.inventory_management.inventory.catalog.category.dtos;

import com.backend.inventory_management.inventory.catalog.category.CategoryEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    private CategoryEntity.CategoryStatus status;
    private Long parentCategoryId;
}
