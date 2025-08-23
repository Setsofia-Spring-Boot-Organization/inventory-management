package com.backend.inventory_management.inventory.catalog.category;

import com.backend.inventory_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @NotBlank
    @Column(unique = true)
    private String categoryCode;
    
    @NotBlank
    private String categoryName;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private CategoryEntity parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryEntity> subcategories = new ArrayList<>();
    
    private String imageUrl;
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.STRING)
    private CategoryStatus status = CategoryStatus.ACTIVE;
    
    public enum CategoryStatus {
        ACTIVE, INACTIVE
    }

    // Constructors
    public CategoryEntity() {}

    public CategoryEntity(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

}