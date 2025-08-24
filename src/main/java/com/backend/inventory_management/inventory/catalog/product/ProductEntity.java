package com.backend.inventory_management.inventory.catalog.product;

import com.backend.inventory_management.common.entity.BaseEntity;
import com.backend.inventory_management.inventory.catalog.brand.BrandEntity;
import com.backend.inventory_management.inventory.catalog.category.CategoryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @NotBlank
    @Column(unique = true)
    private String sku;
    
    @NotBlank
    private String productName;
    
    private String description;
    private String shortDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotBlank
    private CategoryEntity category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;
    
    @NotBlank
    private String barcode;
    
    @NotBlank
    private BigDecimal costPrice;
    
    @NotBlank
    private BigDecimal sellingPrice;
    
    private BigDecimal discountPrice;
    
    @Enumerated(EnumType.STRING)
    private ProductUnit unit = ProductUnit.PIECE;
    
    private Double weight;
    private String dimensions;
    
    private String imageUrl;
    private String additionalImages;
    
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;
    
    private Boolean isPerishable = false;
    private Integer shelfLifeDays;
    
    private Integer minStockLevel = 0;
    private Integer maxStockLevel = 1000;
    private Integer reorderPoint = 10;
    
    private String supplierProductCode;
    private String manufacturerCode;
    
    private LocalDate launchDate;
    private LocalDate discontinuedDate;
    
    private Boolean isTaxable = true;
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    public enum ProductUnit {
        PIECE, KG, GRAM, LITER, MILLILITER, METER, CENTIMETER, PACK, BOX, DOZEN
    }
    
    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED, OUT_OF_STOCK
    }

    // Constructors
    public ProductEntity() {}

    public ProductEntity(String sku, String productName, String barcode, CategoryEntity category, BigDecimal costPrice, BigDecimal sellingPrice) {
        this.sku = sku;
        this.productName = productName;
        this.barcode = barcode;
        this.category = category;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }

}