package com.backend.inventory_management.core.store;

import com.backend.inventory_management.core.store.dtos.StoreCreateRequestDTO;
import com.backend.inventory_management.core.store.dtos.StoreResponseDTO;
import com.backend.inventory_management.core.store.dtos.StoreUpdateRequestDTO;
import com.backend.inventory_management.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<BaseResponse<StoreResponseDTO>> createStore(@Valid @RequestBody StoreCreateRequestDTO createRequest) {
        
        log.info("REST request to create store with code: {}", createRequest.getStoreCode());
        
        StoreResponseDTO createdStore = storeService.createStore(createRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Store created successfully", createdStore));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StoreResponseDTO>> getStoreById(@PathVariable Long id) {
        
        log.debug("REST request to get store by id: {}", id);
        
        StoreResponseDTO store = storeService.getStoreById(id);
        
        return ResponseEntity.ok(BaseResponse.success("Store retrieved successfully", store));
    }

    @GetMapping("/code/{storeCode}")
    public ResponseEntity<BaseResponse<StoreResponseDTO>> getStoreByCode(@PathVariable String storeCode) {
        
        log.debug("REST request to get store by code: {}", storeCode);
        
        StoreResponseDTO store = storeService.getStoreByCode(storeCode);
        
        return ResponseEntity.ok(BaseResponse.success("Store retrieved successfully", store));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<StoreResponseDTO>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "storeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("REST request to get all stores with pagination - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StoreResponseDTO> stores = storeService.getAllStores(pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Stores retrieved successfully", stores));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> getAllActiveStores() {
        
        log.debug("REST request to get all active stores");
        
        List<StoreResponseDTO> stores = storeService.getAllActiveStores();
        
        return ResponseEntity.ok(BaseResponse.success("Active stores retrieved successfully", stores));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> searchStoresByLocation(
            @RequestParam String searchTerm) {
        
        log.debug("REST request to search stores by location: {}", searchTerm);
        
        List<StoreResponseDTO> stores = storeService.searchStoresByLocation(searchTerm);
        
        return ResponseEntity.ok(BaseResponse.success("Stores found successfully", stores));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<BaseResponse<Page<StoreResponseDTO>>> getStoresByTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "storeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("REST request to get stores for tenant: {} with pagination", tenantId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StoreResponseDTO> stores = storeService.getStoresByTenant(tenantId, pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Tenant stores retrieved successfully", stores));
    }

    @GetMapping("/type/{storeType}")
    public ResponseEntity<BaseResponse<Page<StoreResponseDTO>>> getStoresByType(
            @PathVariable StoreEntity.StoreType storeType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "storeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("REST request to get stores by type: {} with pagination", storeType);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StoreResponseDTO> stores = storeService.getStoresByType(storeType, pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Stores by type retrieved successfully", stores));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<Page<StoreResponseDTO>>> getStoresByStatus(
            @PathVariable StoreEntity.StoreStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "storeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("REST request to get stores by status: {} with pagination", status);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StoreResponseDTO> stores = storeService.getStoresByStatus(status, pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Stores by status retrieved successfully", stores));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> getStoresByCity(@PathVariable String city) {
        
        log.debug("REST request to get stores by city: {}", city);
        
        List<StoreResponseDTO> stores = storeService.getStoresByCity(city);
        
        return ResponseEntity.ok(BaseResponse.success("Stores by city retrieved successfully", stores));
    }

    @GetMapping("/filter")
    public ResponseEntity<BaseResponse<Page<StoreResponseDTO>>> filterStores(
            @RequestParam(required = false) StoreEntity.StoreType storeType,
            @RequestParam(required = false) StoreEntity.StoreStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String storeName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "storeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("REST request to filter stores with criteria - type: {}, status: {}, city: {}, name: {}", 
                storeType, status, city, storeName);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StoreResponseDTO> stores = storeService.filterStores(storeType, status, city, storeName, pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Filtered stores retrieved successfully", stores));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StoreResponseDTO>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreUpdateRequestDTO updateRequest) {
        
        log.info("REST request to update store with id: {}", id);
        
        StoreResponseDTO updatedStore = storeService.updateStore(id, updateRequest);
        
        return ResponseEntity.ok(BaseResponse.success("Store updated successfully", updatedStore));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<StoreResponseDTO>> updateStoreStatus(
            @PathVariable Long id,
            @RequestParam StoreEntity.StoreStatus status) {
        
        log.info("REST request to update store status for id: {} to status: {}", id, status);
        
        StoreResponseDTO updatedStore = storeService.updateStoreStatus(id, status);
        
        return ResponseEntity.ok(BaseResponse.success("Store status updated successfully", updatedStore));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStore(@PathVariable Long id) {
        
        log.info("REST request to delete store with id: {}", id);
        
        storeService.deleteStore(id);
        
        return ResponseEntity.ok(BaseResponse.success("Store deleted successfully", null));
    }

    @GetMapping("/exists/{storeCode}")
    public ResponseEntity<BaseResponse<Boolean>> existsByStoreCode(@PathVariable String storeCode) {
        
        log.debug("REST request to check if store code exists: {}", storeCode);
        
        boolean exists = storeService.existsByStoreCode(storeCode);
        
        return ResponseEntity.ok(BaseResponse.success("Store code check completed", exists));
    }

    @GetMapping("/tenant/{tenantId}/count")
    public ResponseEntity<BaseResponse<Long>> getStoreCountByTenant(@PathVariable Long tenantId) {
        
        log.debug("REST request to get store count for tenant: {}", tenantId);
        
        long count = storeService.getStoreCountByTenant(tenantId);
        
        return ResponseEntity.ok(BaseResponse.success("Store count retrieved successfully", count));
    }

//    @GetMapping("/statistics")
//    public ResponseEntity<BaseResponse<StoreStatisticsDTO>> getStoreStatistics(
//            @RequestParam(required = false) Long tenantId) {
//
//        log.debug("REST request to get store statistics for tenant: {}", tenantId);
//
//        StoreStatisticsDTO statistics = storeService.getStoreStatistics(tenantId);
//
//        return ResponseEntity.ok(BaseResponse.success("Statistics retrieved successfully", statistics));
//    }

    @GetMapping("/manager/{managerName}")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> getStoresByManager(
            @PathVariable String managerName) {
        
        log.debug("REST request to get stores by manager: {}", managerName);
        
        List<StoreResponseDTO> stores = storeService.getStoresByManager(managerName);
        
        return ResponseEntity.ok(BaseResponse.success("Stores by manager retrieved successfully", stores));
    }

    @GetMapping("/area-range")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> getStoresByFloorAreaRange(
            @RequestParam Double minArea,
            @RequestParam Double maxArea) {
        
        log.debug("REST request to get stores by floor area range: {} - {}", minArea, maxArea);
        
        List<StoreResponseDTO> stores = storeService.getStoresByFloorAreaRange(minArea, maxArea);
        
        return ResponseEntity.ok(BaseResponse.success("Stores by area range retrieved successfully", stores));
    }

    @PostMapping("/bulk")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> bulkCreateStores(
            @Valid @RequestBody List<StoreCreateRequestDTO> createRequests) {
        
        log.info("REST request to bulk create {} stores", createRequests.size());
        
        List<StoreResponseDTO> createdStores = storeService.bulkCreateStores(createRequests);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Stores created successfully", createdStores));
    }

    @PutMapping("/bulk-status")
    public ResponseEntity<BaseResponse<List<StoreResponseDTO>>> bulkUpdateStoreStatus(
            @RequestBody List<Long> storeIds,
            @RequestParam StoreEntity.StoreStatus status) {
        
        log.info("REST request to bulk update status for {} stores to: {}", storeIds.size(), status);
        
        List<StoreResponseDTO> updatedStores = storeService.bulkUpdateStoreStatus(storeIds, status);
        
        return ResponseEntity.ok(BaseResponse.success("Store statuses updated successfully", updatedStores));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStores(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) Long tenantId) {
        
        log.info("REST request to export stores in format: {} for tenant: {}", format, tenantId);
        
        byte[] exportData = storeService.exportStores(format, tenantId);
        
        String filename = "stores_export." + (format.equalsIgnoreCase("excel") ? "xlsx" : "csv");
        String contentType = format.equalsIgnoreCase("excel") ? 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : 
                "text/csv";
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", contentType)
                .body(exportData);
    }
}