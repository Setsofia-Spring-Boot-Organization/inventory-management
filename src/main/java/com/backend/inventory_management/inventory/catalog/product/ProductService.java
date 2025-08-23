package com.backend.inventory_management.inventory.catalog.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductEntity createProduct(ProductEntity product);
    ProductEntity updateProduct(Long id, ProductEntity product);
    Optional<ProductEntity> findById(Long id);
    Optional<ProductEntity> findBySku(String sku);
    Optional<ProductEntity> findByBarcode(String barcode);
    List<ProductEntity> findAll();
    Page<ProductEntity> findAllWithPagination(Pageable pageable);
    Page<ProductEntity> searchProducts(String search, Pageable pageable);
    List<ProductEntity> findByCategory(Long categoryId);
    List<ProductEntity> findByBrand(Long brandId);
    List<ProductEntity> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductEntity> findActiveProducts();
    List<ProductEntity> findPerishableProducts();
    void deleteProduct(Long id);
    void updateProductStatus(Long id, ProductEntity.ProductStatus status);
    void updateProductPrices(Long id, BigDecimal costPrice, BigDecimal sellingPrice);
    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);
}
