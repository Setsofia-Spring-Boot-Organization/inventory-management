package com.backend.inventory_management.core.tenant.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TenantResponseDTO {
    private Long id;
    private String tenantCode;
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
    private String status;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String businessType;
    private String description;
    private String createdAt;
    private String updatedAt;
}
