package com.backend.inventory_management.inventory.catalog.category;

import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryRequestDto;
import com.backend.inventory_management.inventory.catalog.category.dtos.CategoryResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CategoryResponseDto>> createCategory(
            @Valid @RequestBody CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponseDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponseDto>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponseDto>>> getAllCategories() {
        List<CategoryResponseDto> response = categoryService.getAllCategories();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/root")
    public ResponseEntity<BaseResponse<List<CategoryResponseDto>>> getRootCategories() {
        List<CategoryResponseDto> response = categoryService.getRootCategories();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/subcategories/{parentId}")
    public ResponseEntity<BaseResponse<List<CategoryResponseDto>>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryResponseDto> response = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Category deleted successfully"));
    }
}
