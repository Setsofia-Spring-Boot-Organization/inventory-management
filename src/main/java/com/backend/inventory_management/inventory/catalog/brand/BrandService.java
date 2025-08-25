package com.backend.inventory_management.inventory.catalog.brand;

import com.backend.inventory_management.inventory.catalog.brand.dtos.BrandRequest;
import com.backend.inventory_management.inventory.catalog.brand.dtos.BrandResponse;

import java.util.List;

public interface BrandService {

    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(Long id, BrandRequest request);

    BrandResponse getBrandById(Long id);

    BrandResponse getBrandByCode(String code);

    List<BrandResponse> getAllBrands();

    void deleteBrand(Long id);
}
