package com.backend.inventory_management.core.store;

import com.backend.inventory_management.core.store.dtos.StoreCreateRequestDTO;
import com.backend.inventory_management.core.store.dtos.StoreResponseDTO;
import com.backend.inventory_management.core.store.dtos.StoreUpdateRequestDTO;
import com.backend.inventory_management.core.tenant.TenantEntity;
import com.backend.inventory_management.core.tenant.TenantRepository;
import com.backend.inventory_management.common.exception.ResourceNotFoundException;
import com.backend.inventory_management.common.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final TenantRepository tenantRepository;
    private final StoreMapper storeMapper;

    @Override
    public StoreResponseDTO createStore(StoreCreateRequestDTO createRequest) {
        log.info("Creating store with code: {}", createRequest.getStoreCode());

        // Check if store code already exists
        if (existsByStoreCode(createRequest.getStoreCode())) {
            throw new DuplicateResourceException("Store with code " + createRequest.getStoreCode() + " already exists");
        }

        // Validate tenant exists
        TenantEntity tenant = tenantRepository.findById(createRequest.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + createRequest.getTenantId()));

        StoreEntity store = storeMapper.toEntity(createRequest);
        store.setTenant(tenant);

        StoreEntity savedStore = storeRepository.save(store);
        log.info("Successfully created store with id: {}", savedStore.getId());

        return storeMapper.toResponseDTO(savedStore);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponseDTO getStoreById(Long id) {
        log.debug("Fetching store by id: {}", id);

        StoreEntity store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        return storeMapper.toResponseDTO(store);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponseDTO getStoreByCode(String storeCode) {
        log.debug("Fetching store by code: {}", storeCode);

        StoreEntity store = storeRepository.findByStoreCodeAndIsActiveTrue(storeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Active store not found with code: " + storeCode));

        return storeMapper.toResponseDTO(store);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDTO> getAllStores(Pageable pageable) {
        log.debug("Fetching all stores with pagination");

        Page<StoreEntity> stores = storeRepository.findAll(pageable);
        return stores.map(storeMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getAllActiveStores() {
        log.debug("Fetching all active stores");

        List<StoreEntity> stores = storeRepository.findByIsActiveTrueOrderByStoreName();
        return stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> searchStoresByLocation(String searchTerm) {
        log.debug("Searching stores by location term: {}", searchTerm);

        List<StoreEntity> stores = storeRepository.findBySearchTerm(searchTerm);
        return stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDTO> getStoresByTenant(Long tenantId, Pageable pageable) {
        log.debug("Fetching stores for tenant id: {}", tenantId);

        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + tenantId);
        }

        Page<StoreEntity> stores = storeRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
        return stores.map(storeMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDTO> getStoresByType(StoreEntity.StoreType storeType, Pageable pageable) {
        log.debug("Fetching stores by type: {}", storeType);

        Page<StoreEntity> stores = storeRepository.findByStoreTypeAndIsActiveTrue(storeType, pageable);
        return stores.map(storeMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDTO> getStoresByStatus(StoreEntity.StoreStatus status, Pageable pageable) {
        log.debug("Fetching stores by status: {}", status);

        Page<StoreEntity> stores = storeRepository.findByStatusAndIsActiveTrue(status, pageable);
        return stores.map(storeMapper::toResponseDTO);
    }

    @Override
    public StoreResponseDTO updateStore(Long id, StoreUpdateRequestDTO updateRequest) {
        log.info("Updating store with id: {}", id);

        StoreEntity existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        // Update fields if provided
        storeMapper.updateEntityFromDTO(updateRequest, existingStore);

        StoreEntity updatedStore = storeRepository.save(existingStore);
        log.info("Successfully updated store with id: {}", updatedStore.getId());

        return storeMapper.toResponseDTO(updatedStore);
    }

    @Override
    public StoreResponseDTO updateStoreStatus(Long id, StoreEntity.StoreStatus status) {
        log.info("Updating store status for id: {} to status: {}", id, status);

        StoreEntity store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        store.setStatus(status);
        StoreEntity updatedStore = storeRepository.save(store);

        log.info("Successfully updated store status for id: {}", updatedStore.getId());
        return storeMapper.toResponseDTO(updatedStore);
    }

    @Override
    public void deleteStore(Long id) {
        log.info("Soft deleting store with id: {}", id);

        StoreEntity store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        store.setStatus(StoreEntity.StoreStatus.INACTIVE);
        storeRepository.save(store);

        log.info("Successfully soft deleted store with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStoreCode(String storeCode) {
        return storeRepository.existsByStoreCodeAndIsActiveTrue(storeCode);
    }

    @Override
    @Transactional(readOnly = true)
    public long getStoreCountByTenant(Long tenantId) {
        log.debug("Getting store count for tenant id: {}", tenantId);
        return storeRepository.countByTenantIdAndIsActiveTrue(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getStoresByCity(String city) {
        log.debug("Fetching stores by city: {}", city);

        List<StoreEntity> stores = storeRepository.findByCityIgnoreCaseAndIsActiveTrue(city);
        return stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDTO> filterStores(StoreEntity.StoreType storeType,
                                               StoreEntity.StoreStatus status,
                                               String city,
                                               String storeName,
                                               Pageable pageable) {
        log.debug("Filtering stores with criteria - type: {}, status: {}, city: {}, name: {}",
                storeType, status, city, storeName);

        Page<StoreEntity> stores = storeRepository.findStoresWithFilters(storeType, status, city, storeName, pageable);
        return stores.map(storeMapper::toResponseDTO);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public StoreStatisticsDTO getStoreStatistics(Long tenantId) {
//        log.debug("Getting store statistics for tenant: {}", tenantId);
//
//        List<StoreEntity> stores;
//        if (tenantId != null) {
//            stores = storeRepository.findByTenantIdAndIsActiveTrue(tenantId, Pageable.unpaged()).getContent();
//        } else {
//            stores = storeRepository.findByIsActiveTrueOrderByStoreName();
//        }
//
//        return buildStatistics(stores, tenantId);
//    }
//
//    private StoreStatisticsDTO buildStatistics(List<StoreEntity> stores, Long tenantId) {
//        if (stores.isEmpty()) {
//            return StoreStatisticsDTO.builder().build();
//        }
//
//        // Status counts
//        Map<String, Long> statusCounts = stores.stream()
//                .collect(Collectors.groupingBy(
//                        store -> store.getStatus().name(),
//                        Collectors.counting()));
//
//        // Type counts
//        Map<String, Long> typeCounts = stores.stream()
//                .collect(Collectors.groupingBy(
//                        store -> store.getStoreType().name(),
//                        Collectors.counting()));
//
//        // City counts
//        Map<String, Long> cityCounts = stores.stream()
//                .filter(store -> store.getCity() != null)
//                .collect(Collectors.groupingBy(
//                        StoreEntity::getCity,
//                        Collectors.counting()));
//
//        // Floor area statistics
//        List<Double> floorAreas = stores.stream()
//                .map(StoreEntity::getFloorArea)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        Double avgFloorArea = floorAreas.stream()
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElse(0.0);
//
//        Double totalFloorArea = floorAreas.stream()
//                .mapToDouble(Double::doubleValue)
//                .sum();
//
//        Double minFloorArea = floorAreas.stream()
//                .mapToDouble(Double::doubleValue)
//                .min()
//                .orElse(0.0);
//
//        Double maxFloorArea = floorAreas.stream()
//                .mapToDouble(Double::doubleValue)
//                .max()
//                .orElse(0.0);
//
//        // Number of floors statistics
//        List<Integer> numberOfFloors = stores.stream()
//                .map(StoreEntity::getNumberOfFloors)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        Double avgNumberOfFloors = numberOfFloors.stream()
//                .mapToDouble(Integer::doubleValue)
//                .average()
//                .orElse(0.0);
//
//        Integer minNumberOfFloors = numberOfFloors.stream()
//                .mapToInt(Integer::intValue)
//                .min()
//                .orElse(0);
//
//        Integer maxNumberOfFloors = numberOfFloors.stream()
//                .mapToInt(Integer::intValue)
//                .max()
//                .orElse(0);
//
//        // Manager statistics
//        long storesWithManagers = stores.stream()
//                .filter(store -> store.getManagerName() != null && !store.getManagerName().trim().isEmpty())
//                .count();
//
//        long storesWithoutManagers = stores.size() - storesWithManagers;
//
//        // Largest and smallest stores by area
//        String largestStoreByArea = stores.stream()
//                .filter(store -> store.getFloorArea() != null)
//                .max(Comparator.comparing(StoreEntity::getFloorArea))
//                .map(StoreEntity::getStoreName)
//                .orElse("N/A");
//
//        String smallestStoreByArea = stores.stream()
//                .filter(store -> store.getFloorArea() != null)
//                .min(Comparator.comparing(StoreEntity::getFloorArea))
//                .map(StoreEntity::getStoreName)
//                .orElse("N/A");
//
//        // Tenant information
//        String tenantName = null;
//        if (tenantId != null && !stores.isEmpty()) {
//            tenantName = stores.get(0).getTenant().getTenantName();
//        }
//
//        return StoreStatisticsDTO.builder()
//                .totalStores(stores.size())
//                .activeStores(statusCounts.getOrDefault("ACTIVE", 0L))
//                .inactiveStores(statusCounts.getOrDefault("INACTIVE", 0L))
//                .maintenanceStores(statusCounts.getOrDefault("MAINTENANCE", 0L))
//                .closedStores(statusCounts.getOrDefault("CLOSED", 0L))
//                .storesByType(typeCounts)
//                .storesByCity(cityCounts)
//                .storesByStatus(statusCounts)
//                .averageFloorArea(avgFloorArea)
//                .totalFloorArea(totalFloorArea)
//                .minFloorArea(minFloorArea)
//                .maxFloorArea(maxFloorArea)
//                .averageNumberOfFloors(avgNumberOfFloors.intValue())
//                .minNumberOfFloors(minNumberOfFloors)
//                .maxNumberOfFloors(maxNumberOfFloors)
//                .storesWithManagers(storesWithManagers)
//                .storesWithoutManagers(storesWithoutManagers)
//                .largestStoreByArea(largestStoreByArea)
//                .smallestStoreByArea(smallestStoreByArea)
//                .tenantId(tenantId)
//                .tenantName(tenantName)
//                .build();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getStoresByManager(String managerName) {
        log.debug("Fetching stores by manager: {}", managerName);

        List<StoreEntity> stores = storeRepository.findByManagerNameContainingIgnoreCaseAndIsActiveTrue(managerName);
        return stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getStoresByFloorAreaRange(Double minArea, Double maxArea) {
        log.debug("Fetching stores by floor area range: {} - {}", minArea, maxArea);

        if (minArea == null || maxArea == null || minArea < 0 || maxArea < 0 || minArea > maxArea) {
            throw new IllegalArgumentException("Invalid floor area range parameters");
        }

        List<StoreEntity> stores = storeRepository.findByFloorAreaRange(minArea, maxArea);
        return stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StoreResponseDTO> bulkCreateStores(List<StoreCreateRequestDTO> createRequests) {
        log.info("Bulk creating {} stores", createRequests.size());

        if (createRequests.isEmpty()) {
            throw new IllegalArgumentException("Store creation requests cannot be empty");
        }

        // Validate all store codes are unique
        Set<String> storeCodes = createRequests.stream()
                .map(StoreCreateRequestDTO::getStoreCode)
                .collect(Collectors.toSet());

        if (storeCodes.size() != createRequests.size()) {
            throw new DuplicateResourceException("Duplicate store codes found in the request");
        }

        // Check for existing store codes
        List<String> existingCodes = storeCodes.stream()
                .filter(this::existsByStoreCode)
                .collect(Collectors.toList());

        if (!existingCodes.isEmpty()) {
            throw new DuplicateResourceException("Store codes already exist: " + String.join(", ", existingCodes));
        }

        List<StoreResponseDTO> createdStores = new ArrayList<>();

        for (StoreCreateRequestDTO request : createRequests) {
            try {
                StoreResponseDTO createdStore = createStore(request);
                createdStores.add(createdStore);
            } catch (Exception e) {
                log.error("Failed to create store with code: {}, error: {}", request.getStoreCode(), e.getMessage());
                // Continue with other stores, but log the error
            }
        }

        log.info("Successfully bulk created {}/{} stores", createdStores.size(), createRequests.size());
        return createdStores;
    }

    @Override
    @Transactional
    public List<StoreResponseDTO> bulkUpdateStoreStatus(List<Long> storeIds, StoreEntity.StoreStatus status) {
        log.info("Bulk updating status for {} stores to: {}", storeIds.size(), status);

        if (storeIds.isEmpty()) {
            throw new IllegalArgumentException("Store IDs cannot be empty");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        List<StoreResponseDTO> updatedStores = new ArrayList<>();

        for (Long storeId : storeIds) {
            try {
                StoreResponseDTO updatedStore = updateStoreStatus(storeId, status);
                updatedStores.add(updatedStore);
            } catch (Exception e) {
                log.error("Failed to update status for store with id: {}, error: {}", storeId, e.getMessage());
                // Continue with other stores, but log the error
            }
        }

        log.info("Successfully updated status for {}/{} stores", updatedStores.size(), storeIds.size());
        return updatedStores;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportStores(String format, Long tenantId) {
        log.info("Exporting stores in format: {} for tenant: {}", format, tenantId);

        List<StoreEntity> stores;
        if (tenantId != null) {
            stores = storeRepository.findByTenantIdAndIsActiveTrue(tenantId, Pageable.unpaged()).getContent();
        } else {
            stores = storeRepository.findByIsActiveTrueOrderByStoreName();
        }

        List<StoreResponseDTO> storeData = stores.stream()
                .map(storeMapper::toResponseDTO)
                .collect(Collectors.toList());

        try {
            if ("excel".equalsIgnoreCase(format)) {
                return exportToExcel(storeData);
            } else {
                return exportToCsv(storeData);
            }
        } catch (Exception e) {
            log.error("Failed to export stores in format: {}", format, e);
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }

    private byte[] exportToCsv(List<StoreResponseDTO> stores) throws Exception {
        StringWriter writer = new StringWriter();

        // Write CSV header
        writer.append("Store Code,Store Name,Description,Address,City,State,Country,Postal Code,")
                .append("Phone Number,Email,Manager Name,Store Type,Status,Floor Area,Number of Floors,")
                .append("Tenant Name,Created At,Updated At\n");

        // Write data rows
        for (StoreResponseDTO store : stores) {
            writer.append(escapeCsvField(store.getStoreCode())).append(",")
                    .append(escapeCsvField(store.getStoreName())).append(",")
                    .append(escapeCsvField(store.getDescription())).append(",")
                    .append(escapeCsvField(store.getAddress())).append(",")
                    .append(escapeCsvField(store.getCity())).append(",")
                    .append(escapeCsvField(store.getState())).append(",")
                    .append(escapeCsvField(store.getCountry())).append(",")
                    .append(escapeCsvField(store.getPostalCode())).append(",")
                    .append(escapeCsvField(store.getPhoneNumber())).append(",")
                    .append(escapeCsvField(store.getEmail())).append(",")
                    .append(escapeCsvField(store.getManagerName())).append(",")
                    .append(store.getStoreType() != null ? store.getStoreType().name() : "").append(",")
                    .append(store.getStatus() != null ? store.getStatus().name() : "").append(",")
                    .append(store.getFloorArea() != null ? store.getFloorArea().toString() : "").append(",")
                    .append(store.getNumberOfFloors() != null ? store.getNumberOfFloors().toString() : "").append(",")
                    .append(escapeCsvField(store.getTenantName())).append(",")
                    .append(store.getCreatedAt() != null ? store.getCreatedAt().toString() : "").append(",")
                    .append(store.getUpdatedAt() != null ? store.getUpdatedAt().toString() : "").append("\n");
        }

        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] exportToExcel(List<StoreResponseDTO> stores) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stores");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Store Code", "Store Name", "Description", "Address", "City", "State", "Country",
                "Postal Code", "Phone Number", "Email", "Manager Name", "Store Type", "Status",
                "Floor Area", "Number of Floors", "Tenant Name", "Created At", "Updated At"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            // Style header cells
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        for (int i = 0; i < stores.size(); i++) {
            StoreResponseDTO store = stores.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(store.getStoreCode());
            row.createCell(1).setCellValue(store.getStoreName());
            row.createCell(2).setCellValue(store.getDescription());
            row.createCell(3).setCellValue(store.getAddress());
            row.createCell(4).setCellValue(store.getCity());
            row.createCell(5).setCellValue(store.getState());
            row.createCell(6).setCellValue(store.getCountry());
            row.createCell(7).setCellValue(store.getPostalCode());
            row.createCell(8).setCellValue(store.getPhoneNumber());
            row.createCell(9).setCellValue(store.getEmail());
            row.createCell(10).setCellValue(store.getManagerName());
            row.createCell(11).setCellValue(store.getStoreType() != null ? store.getStoreType().name() : "");
            row.createCell(12).setCellValue(store.getStatus() != null ? store.getStatus().name() : "");
            row.createCell(13).setCellValue(store.getFloorArea() != null ? store.getFloorArea() : 0.0);
            row.createCell(14).setCellValue(store.getNumberOfFloors() != null ? store.getNumberOfFloors() : 0);
            row.createCell(15).setCellValue(store.getTenantName());
            row.createCell(16).setCellValue(store.getCreatedAt() != null ? store.getCreatedAt().toString() : "");
            row.createCell(17).setCellValue(store.getUpdatedAt() != null ? store.getUpdatedAt().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }
}