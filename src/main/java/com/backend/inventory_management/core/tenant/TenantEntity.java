package com.backend.inventory_management.core.tenant;

import com.backend.inventory_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "tenants")
public class TenantEntity extends BaseEntity {

    @NotBlank
    @Column(unique = true)
    private String tenantCode;

    @NotBlank
    private String tenantName;

    private String businessRegistrationNumber;
    private String taxNumber;

    private String contactPerson;
    private String contactEmail;
    private String contactPhone;

    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ACTIVE;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private String businessType;
    private String description;

    public enum TenantStatus {
        ACTIVE, INACTIVE, SUSPENDED, CONTRACT_EXPIRED
    }

    // Constructors
    public TenantEntity() {}

    public TenantEntity(String tenantCode, String tenantName, String contactPerson) {
        this.tenantCode = tenantCode;
        this.tenantName = tenantName;
        this.contactPerson = contactPerson;
    }

}
