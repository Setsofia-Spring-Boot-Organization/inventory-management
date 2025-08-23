package com.backend.inventory_management.inventory.catalog.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findBySku(String sku);
    Optional<ProductEntity> findByBarcode(String barcode);
    List<ProductEntity> findByCategoryId(Long categoryId);
    List<ProductEntity> findByBrandId(Long brandId);
    List<ProductEntity> findByStatus(ProductEntity.ProductStatus status);
    List<ProductEntity> findByIsPerishableTrue();

    @Query("SELECT p FROM ProductEntity p WHERE p.status = 'ACTIVE' AND p.isActive = true")
    List<ProductEntity> findActiveProducts();

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:search IS NULL OR " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductEntity> findProductsWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "p.sellingPrice BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<ProductEntity> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id = :categoryId AND p.isActive = true")
    List<ProductEntity> findActiveByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.brand.id = :brandId AND p.isActive = true")
    List<ProductEntity> findActiveByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.category.id = :categoryId AND p.isActive = true")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.brand.id = :brandId AND p.isActive = true")
    Long countByBrandId(@Param("brandId") Long brandId);

    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);

    // Additional useful queries
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<ProductEntity> findRecentProducts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.discountPrice IS NOT NULL AND p.isActive = true")
    List<ProductEntity> findProductsWithDiscount();

    @Query("SELECT p FROM ProductEntity p WHERE p.status = :status AND p.isActive = true")
    Page<ProductEntity> findByStatusWithPagination(@Param("status") ProductEntity.ProductStatus status, Pageable pageable);
}