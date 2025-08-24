package com.backend.inventory_management.core.store.dtos;

import com.backend.inventory_management.core.store.StoreEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreCreateRequestDTO {

    @NotBlank(message = "Store code is required")
    @Size(max = 50, message = "Store code must not exceed 50 characters")
    private String storeCode;

    @NotBlank(message = "Store name is required")
    @Size(max = 255, message = "Store name must not exceed 255 characters")
    private String storeName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 255, message = "Manager name must not exceed 255 characters")
    private String managerName;

    @NotNull(message = "Store type is required")
    private StoreEntity.StoreType storeType;

    private StoreEntity.StoreStatus status = StoreEntity.StoreStatus.ACTIVE;

    private Double floorArea;

    private Integer numberOfFloors;

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;
}


