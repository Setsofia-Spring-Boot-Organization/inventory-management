package com.backend.inventory_management.inventory.catalog.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    Optional<CategoryEntity> findByCategoryCode(String categoryCode);
    List<CategoryEntity> findByParentCategoryIsNull();
    List<CategoryEntity> findByParentCategoryId(Long parentId);
    List<CategoryEntity> findByStatus(CategoryEntity.CategoryStatus status);
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.parentCategory IS NULL ORDER BY c.sortOrder, c.categoryName")
    List<CategoryEntity> findRootCategoriesOrdered();
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.parentCategory.id = ?1 ORDER BY c.sortOrder, c.categoryName")
    List<CategoryEntity> findSubcategoriesOrdered(Long parentId);
    
    boolean existsByCategoryCode(String categoryCode);
}
