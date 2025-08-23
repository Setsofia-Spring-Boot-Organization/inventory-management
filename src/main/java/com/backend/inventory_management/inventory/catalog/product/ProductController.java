package com.backend.inventory_management.inventory.catalog.product;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.common.dto.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<ProductEntity>> createProduct(@Valid @RequestBody ProductEntity product) {
        try {
            ProductEntity createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.success(createdProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductEntity>> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(BaseResponse.success(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<BaseResponse<ProductEntity>> getProductBySku(@PathVariable String sku) {
        return productService.findBySku(sku)
                .map(product -> ResponseEntity.ok(BaseResponse.success(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<BaseResponse<ProductEntity>> getProductByBarcode(@PathVariable String barcode) {
        return productService.findByBarcode(barcode)
                .map(product -> ResponseEntity.ok(BaseResponse.success(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ProductEntity>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> products = search != null && !search.isEmpty() ?
                productService.searchProducts(search, pageable) :
                productService.findAllWithPagination(pageable);

        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BaseResponse<List<ProductEntity>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductEntity> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<BaseResponse<List<ProductEntity>>> getProductsByBrand(@PathVariable Long brandId) {
        List<ProductEntity> products = productService.findByBrand(brandId);
        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<List<ProductEntity>>> getActiveProducts() {
        List<ProductEntity> products = productService.findActiveProducts();
        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @GetMapping("/perishable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('INVENTORY_CLERK')")
    public ResponseEntity<BaseResponse<List<ProductEntity>>> getPerishableProducts() {
        List<ProductEntity> products = productService.findPerishableProducts();
        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @GetMapping("/price-range")
    public ResponseEntity<BaseResponse<List<ProductEntity>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<ProductEntity> products = productService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(BaseResponse.success(products));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<ProductEntity>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductEntity product) {
        try {
            ProductEntity updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(BaseResponse.success(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<Void>> updateProductStatus(
            @PathVariable Long id, @RequestParam ProductEntity.ProductStatus status) {
        try {
            productService.updateProductStatus(id, status);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/prices")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<Void>> updateProductPrices(
            @PathVariable Long id,
            @RequestParam BigDecimal costPrice,
            @RequestParam BigDecimal sellingPrice) {
        try {
            productService.updateProductPrices(id, costPrice, sellingPrice);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }
}
