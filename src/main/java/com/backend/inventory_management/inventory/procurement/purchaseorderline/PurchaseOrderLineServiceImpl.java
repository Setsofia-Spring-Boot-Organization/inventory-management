package com.backend.inventory_management.inventory.procurement.purchaseorderline;

import com.backend.inventory_management.inventory.catalog.product.ProductEntity;
import com.backend.inventory_management.inventory.catalog.product.ProductRepository;
import com.backend.inventory_management.inventory.procurement.purchaseorder.PurchaseOrderEntity;
import com.backend.inventory_management.inventory.procurement.purchaseorder.PurchaseOrderRepository;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineRequest;
import com.backend.inventory_management.inventory.procurement.purchaseorderline.dtos.PurchaseOrderLineResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderLineServiceImpl implements PurchaseOrderLineService {

    @Autowired
    private PurchaseOrderLineRepository repository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public PurchaseOrderLineResponse create(PurchaseOrderLineRequest request) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found"));

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        PurchaseOrderLineEntity entity = new PurchaseOrderLineEntity(
                purchaseOrder,
                product,
                request.getLineNumber(),
                request.getQuantity(),
                request.getUnitPrice()
        );
        entity.setDiscountPercent(request.getDiscountPercent());
        entity.setDiscountAmount(request.getDiscountAmount());
        entity.setNotes(request.getNotes());
        entity.setSupplierProductCode(request.getSupplierProductCode());

        PurchaseOrderLineEntity saved = repository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public PurchaseOrderLineResponse update(Long id, PurchaseOrderLineRequest request) {
        PurchaseOrderLineEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrderLine not found"));

        entity.setQuantity(request.getQuantity());
        entity.setUnitPrice(request.getUnitPrice());
        entity.setDiscountPercent(request.getDiscountPercent());
        entity.setDiscountAmount(request.getDiscountAmount());
        entity.setNotes(request.getNotes());
        entity.setSupplierProductCode(request.getSupplierProductCode());

        PurchaseOrderLineEntity updated = repository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public PurchaseOrderLineResponse getById(Long id) {
        PurchaseOrderLineEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrderLine not found"));
        return mapToResponse(entity);
    }

    @Override
    public List<PurchaseOrderLineResponse> getByPurchaseOrder(Long purchaseOrderId) {
        return repository.findByPurchaseOrderId(purchaseOrderId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PurchaseOrderLineResponse mapToResponse(PurchaseOrderLineEntity entity) {
        PurchaseOrderLineResponse response = new PurchaseOrderLineResponse();
        response.setId(entity.getId());
        response.setPurchaseOrderId(entity.getPurchaseOrder().getId());
        response.setProductId(entity.getProduct().getId());
        response.setLineNumber(entity.getLineNumber());
        response.setQuantity(entity.getQuantity());
        response.setReceivedQuantity(entity.getReceivedQuantity());
        response.setRemainingQuantity(entity.getRemainingQuantity());
        response.setUnitPrice(entity.getUnitPrice());
        response.setLineTotal(entity.getLineTotal());
        response.setDiscountPercent(entity.getDiscountPercent());
        response.setDiscountAmount(entity.getDiscountAmount());
        response.setNotes(entity.getNotes());
        response.setSupplierProductCode(entity.getSupplierProductCode());
        return response;
    }
}
