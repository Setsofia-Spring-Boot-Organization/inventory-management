package com.backend.inventory_management.inventory.catalog.category;

import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryRequestDto;
import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryResponseDto mapToDto(CategoryEntity entity) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryCode(entity.getCategoryCode());
        dto.setCategoryName(entity.getCategoryName());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setSortOrder(entity.getSortOrder());
        dto.setStatus(entity.getStatus());
        dto.setParentCategoryId(entity.getParentCategory() != null ? entity.getParentCategory().getId() : null);
        return dto;
    }

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryCode(request.getCategoryCode());
        entity.setCategoryName(request.getCategoryName());
        entity.setDescription(request.getDescription());
        entity.setImageUrl(request.getImageUrl());
        entity.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        if (request.getParentCategoryId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            entity.setParentCategory(parent);
        }

        CategoryEntity saved = categoryRepository.save(entity);
        return mapToDto(saved);
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        entity.setCategoryCode(request.getCategoryCode());
        entity.setCategoryName(request.getCategoryName());
        entity.setDescription(request.getDescription());
        entity.setImageUrl(request.getImageUrl());
        entity.setSortOrder(request.getSortOrder());

        if (request.getParentCategoryId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            entity.setParentCategory(parent);
        } else {
            entity.setParentCategory(null);
        }

        return mapToDto(categoryRepository.save(entity));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return mapToDto(entity);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDto> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId).stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
