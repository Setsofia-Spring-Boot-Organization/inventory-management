package com.backend.inventory_management.inventory.procurement.supplier;

import com.backend.inventory_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "suppliers")
@Data
public class SupplierEntity extends BaseEntity {
    
    @NotBlank
    @Column(unique = true)
    private String supplierCode;
    
    @NotBlank
    private String supplierName;
    
    private String companyRegistrationNumber;
    private String taxNumber;
    
    @NotBlank
    private String contactPerson;
    
    private String phoneNumber;
    
    @Email
    private String email;
    
    @NotBlank
    private String address;
    
    private String city;
    private String state;
    private String country;
    private String postalCode;
    
    private String website;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SupplierType supplierType = SupplierType.VENDOR;
    
    @Enumerated(EnumType.STRING)
    private SupplierStatus status = SupplierStatus.ACTIVE;
    
    private Integer paymentTermsDays = 30;
    private BigDecimal creditLimit = BigDecimal.ZERO;
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private Rating rating = Rating.NOT_RATED;
    
    private String bankAccountDetails;
    private String notes;
    
    public enum SupplierType {
        VENDOR, MANUFACTURER, DISTRIBUTOR, WHOLESALER, SERVICE_PROVIDER
    }
    
    public enum SupplierStatus {
        ACTIVE, INACTIVE, SUSPENDED, BLACKLISTED
    }
    
    public enum Rating {
        EXCELLENT, GOOD, AVERAGE, POOR, NOT_RATED
    }

    // Constructors
    public SupplierEntity() {}

    public SupplierEntity(String supplierCode, String supplierName, String contactPerson) {
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
    }
}