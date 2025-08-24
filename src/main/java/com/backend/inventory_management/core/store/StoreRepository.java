package com.backend.inventory_management.core.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    @Query("SELECT s FROM StoreEntity s WHERE s.isActive = true AND " +
           "(LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<StoreEntity> findBySearchTerm(@Param("search") String search);

    // Find by store code (active only)
    Optional<StoreEntity> findByStoreCodeAndIsActiveTrue(String storeCode);

    // Check if store code exists (active only)
    boolean existsByStoreCodeAndIsActiveTrue(String storeCode);

    // Find all active stores ordered by name
    List<StoreEntity> findByIsActiveTrueOrderByStoreName();

    // Find stores by tenant
    Page<StoreEntity> findByTenantIdAndIsActiveTrue(Long tenantId, Pageable pageable);

    // Find stores by type
    Page<StoreEntity> findByStoreTypeAndIsActiveTrue(StoreEntity.StoreType storeType, Pageable pageable);

    // Find stores by status
    Page<StoreEntity> findByStatusAndIsActiveTrue(StoreEntity.StoreStatus status, Pageable pageable);

    // Find stores by city
    List<StoreEntity> findByCityIgnoreCaseAndIsActiveTrue(String city);

    // Count stores by tenant
    long countByTenantIdAndIsActiveTrue(Long tenantId);

    // Find stores by tenant and status
    @Query("SELECT s FROM StoreEntity s WHERE s.tenant.id = :tenantId AND s.status = :status AND s.isActive = true")
    List<StoreEntity> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") StoreEntity.StoreStatus status);

    // Find stores by multiple cities
    @Query("SELECT s FROM StoreEntity s WHERE LOWER(s.city) IN :cities AND s.isActive = true")
    List<StoreEntity> findByCitiesIgnoreCase(@Param("cities") List<String> cities);

    // Advanced search with multiple criteria
    @Query("SELECT s FROM StoreEntity s WHERE s.isActive = true AND " +
           "(:storeType IS NULL OR s.storeType = :storeType) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:storeName IS NULL OR LOWER(s.storeName) LIKE LOWER(CONCAT('%', :storeName, '%')))")
    Page<StoreEntity> findStoresWithFilters(@Param("storeType") StoreEntity.StoreType storeType,
                                           @Param("status") StoreEntity.StoreStatus status,
                                           @Param("city") String city,
                                           @Param("storeName") String storeName,
                                           Pageable pageable);

    // Find stores within floor area range
    @Query("SELECT s FROM StoreEntity s WHERE s.isActive = true AND " +
           "s.floorArea BETWEEN :minArea AND :maxArea")
    List<StoreEntity> findByFloorAreaRange(@Param("minArea") Double minArea, @Param("maxArea") Double maxArea);

    // Find stores by manager name
    List<StoreEntity> findByManagerNameContainingIgnoreCaseAndIsActiveTrue(String managerName);
}