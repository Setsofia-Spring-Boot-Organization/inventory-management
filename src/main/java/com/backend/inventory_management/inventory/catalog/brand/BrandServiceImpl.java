package com.backend.inventory_management.inventory.catalog.brand;

import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import com.backend.inventory_management.inventory.catalog.brand.dtos.BrandRequest;
import com.backend.inventory_management.inventory.catalog.brand.dtos.BrandResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    private BrandResponse mapToResponse(BrandEntity entity) {
        return BrandResponse.builder()
                .id(entity.getId())
                .brandCode(entity.getBrandCode())
                .brandName(entity.getBrandName())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .website(entity.getWebsite())
                .contactInfo(entity.getContactInfo())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        BrandEntity brand = new BrandEntity();
        brand.setBrandCode(request.getBrandCode());
        brand.setBrandName(request.getBrandName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setWebsite(request.getWebsite());
        brand.setContactInfo(request.getContactInfo());
        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        BrandEntity brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        brand.setBrandCode(request.getBrandCode());
        brand.setBrandName(request.getBrandName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setWebsite(request.getWebsite());
        brand.setContactInfo(request.getContactInfo());
        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
    }

    @Override
    public BrandResponse getBrandByCode(String code) {
        return brandRepository.findByBrandCode(code)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with code: " + code));
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }
}
