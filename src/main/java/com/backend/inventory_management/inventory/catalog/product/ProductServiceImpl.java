package com.backend.inventory_management.inventory.catalog.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductEntity createProduct(ProductEntity product) {
        if (existsBySku(product.getSku())) {
            throw new RuntimeException("Product with SKU " + product.getSku() + " already exists");
        }
        if (existsByBarcode(product.getBarcode())) {
            throw new RuntimeException("Product with barcode " + product.getBarcode() + " already exists");
        }
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductEntity updateProduct(Long id, ProductEntity product) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if SKU is being changed and if new SKU already exists
        if (!existingProduct.getSku().equals(product.getSku()) && existsBySku(product.getSku())) {
            throw new RuntimeException("Product with SKU " + product.getSku() + " already exists");
        }

        // Check if barcode is being changed and if new barcode already exists
        if (!existingProduct.getBarcode().equals(product.getBarcode()) && existsByBarcode(product.getBarcode())) {
            throw new RuntimeException("Product with barcode " + product.getBarcode() + " already exists");
        }

        existingProduct.setSku(product.getSku());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setShortDescription(product.getShortDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setCostPrice(product.getCostPrice());
        existingProduct.setSellingPrice(product.getSellingPrice());
        existingProduct.setDiscountPrice(product.getDiscountPrice());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setWeight(product.getWeight());
        existingProduct.setDimensions(product.getDimensions());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setIsPerishable(product.getIsPerishable());
        existingProduct.setShelfLifeDays(product.getShelfLifeDays());
        existingProduct.setMinStockLevel(product.getMinStockLevel());
        existingProduct.setMaxStockLevel(product.getMaxStockLevel());
        existingProduct.setReorderPoint(product.getReorderPoint());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#sku")
    public Optional<ProductEntity> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductEntity> findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductEntity> findAllWithPagination(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductEntity> searchProducts(String search, Pageable pageable) {
        return productRepository.findProductsWithSearch(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findPerishableProducts() {
        return productRepository.findByIsPerishableTrue();
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);
        product.setStatus(ProductEntity.ProductStatus.DISCONTINUED);
        productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void updateProductStatus(Long id, ProductEntity.ProductStatus status) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(status);
        productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void updateProductPrices(Long id, BigDecimal costPrice, BigDecimal sellingPrice) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setCostPrice(costPrice);
        product.setSellingPrice(sellingPrice);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBarcode(String barcode) {
        return productRepository.existsByBarcode(barcode);
    }
}
