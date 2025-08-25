package com.backend.inventory_management.inventory.catalog.category;

import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryRequestDto;
import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto request);
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto request);
    void deleteCategory(Long id);
    CategoryResponseDto getCategoryById(Long id);
    List<CategoryResponseDto> getAllCategories();
    List<CategoryResponseDto> getRootCategories();
    List<CategoryResponseDto> getSubcategories(Long parentId);
}
