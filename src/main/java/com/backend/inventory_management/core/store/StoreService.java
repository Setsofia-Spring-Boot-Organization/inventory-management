package com.backend.inventory_management.core.store;

import com.backend.inventory_management.core.store.dtos.StoreCreateRequestDTO;
import com.backend.inventory_management.core.store.dtos.StoreResponseDTO;
import com.backend.inventory_management.core.store.dtos.StoreUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {

    /**
     * Create a new store
     * @param createRequest Store creation request
     * @return Created store response
     */
    StoreResponseDTO createStore(StoreCreateRequestDTO createRequest);

    /**
     * Get store by ID
     * @param id Store ID
     * @return Store response
     */
    StoreResponseDTO getStoreById(Long id);

    /**
     * Get store by store code
     * @param storeCode Store code
     * @return Store response
     */
    StoreResponseDTO getStoreByCode(String storeCode);

    /**
     * Get all stores with pagination
     * @param pageable Pagination information
     * @return Page of stores
     */
    Page<StoreResponseDTO> getAllStores(Pageable pageable);

    /**
     * Get all active stores
     * @return List of active stores
     */
    List<StoreResponseDTO> getAllActiveStores();

    /**
     * Search stores by location (city or address)
     * @param searchTerm Search term
     * @return List of matching stores
     */
    List<StoreResponseDTO> searchStoresByLocation(String searchTerm);

    /**
     * Get stores by tenant ID
     * @param tenantId Tenant ID
     * @param pageable Pagination information
     * @return Page of stores for the tenant
     */
    Page<StoreResponseDTO> getStoresByTenant(Long tenantId, Pageable pageable);

    /**
     * Get stores by type
     * @param storeType Store type
     * @param pageable Pagination information
     * @return Page of stores by type
     */
    Page<StoreResponseDTO> getStoresByType(StoreEntity.StoreType storeType, Pageable pageable);

    /**
     * Get stores by status
     * @param status Store status
     * @param pageable Pagination information
     * @return Page of stores by status
     */
    Page<StoreResponseDTO> getStoresByStatus(StoreEntity.StoreStatus status, Pageable pageable);

    /**
     * Update store
     * @param id Store ID
     * @param updateRequest Update request
     * @return Updated store response
     */
    StoreResponseDTO updateStore(Long id, StoreUpdateRequestDTO updateRequest);

    /**
     * Update store status
     * @param id Store ID
     * @param status New status
     * @return Updated store response
     */
    StoreResponseDTO updateStoreStatus(Long id, StoreEntity.StoreStatus status);

    /**
     * Soft delete store
     * @param id Store ID
     */
    void deleteStore(Long id);

    /**
     * Check if store code exists
     * @param storeCode Store code
     * @return true if exists, false otherwise
     */
    boolean existsByStoreCode(String storeCode);

    /**
     * Get total number of stores for tenant
     * @param tenantId Tenant ID
     * @return Count of stores
     */
    long getStoreCountByTenant(Long tenantId);

    /**
     * Get stores by city
     * @param city City name
     * @return List of stores in the city
     */
    List<StoreResponseDTO> getStoresByCity(String city);

    // Additional methods to add to StoreService interface

    /**
     * Advanced filtering with multiple criteria
     * @param storeType Store type filter
     * @param status Store status filter
     * @param city City filter
     * @param storeName Store name filter
     * @param pageable Pagination information
     * @return Page of filtered stores
     */
    Page<StoreResponseDTO> filterStores(StoreEntity.StoreType storeType,
                                        StoreEntity.StoreStatus status,
                                        String city,
                                        String storeName,
                                        Pageable pageable);

//    /**
//     * Get store statistics
//     * @param tenantId Optional tenant ID filter
//     * @return Store statistics
//     */
//    StoreStatisticsDTO getStoreStatistics(Long tenantId);

    /**
     * Get stores by manager name
     * @param managerName Manager name
     * @return List of stores managed by the manager
     */
    List<StoreResponseDTO> getStoresByManager(String managerName);

    /**
     * Get stores within floor area range
     * @param minArea Minimum floor area
     * @param maxArea Maximum floor area
     * @return List of stores within the area range
     */
    List<StoreResponseDTO> getStoresByFloorAreaRange(Double minArea, Double maxArea);

    /**
     * Bulk create stores
     * @param createRequests List of store creation requests
     * @return List of created stores
     */
    List<StoreResponseDTO> bulkCreateStores(List<StoreCreateRequestDTO> createRequests);

    /**
     * Bulk update store status
     * @param storeIds List of store IDs
     * @param status New status
     * @return List of updated stores
     */
    List<StoreResponseDTO> bulkUpdateStoreStatus(List<Long> storeIds, StoreEntity.StoreStatus status);

    /**
     * Export stores data
     * @param format Export format (csv/excel)
     * @param tenantId Optional tenant filter
     * @return Export data as byte array
     */
    byte[] exportStores(String format, Long tenantId);
}