// DataSeederService.java
package com.backend.inventory_management.features.data_seeder;

import com.backend.inventory_management.features.auth.Role;
import com.backend.inventory_management.features.auth.User;
import com.backend.inventory_management.features.auth.UserRepository;
import com.backend.inventory_management.features.inventory.InventoryItem;
import com.backend.inventory_management.features.inventory.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeederService implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsers();
            log.info("Sample users created successfully");
        }
        
        if (inventoryItemRepository.count() == 0) {
            seedInventoryItems();
            log.info("Sample inventory items created successfully");
        }
    }
    
    private void seedUsers() {
        User admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .email("admin@inventorymanager.com")
            .firstName("System")
            .lastName("Administrator")
            .role(Role.ADMIN)
            .enabled(true)
            .build();
        
        User manager = User.builder()
            .username("manager")
            .password(passwordEncoder.encode("manager123"))
            .email("manager@inventorymanager.com")
            .firstName("John")
            .lastName("Manager")
            .role(Role.MANAGER)
            .enabled(true)
            .build();
        
        User user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("user123"))
            .email("user@inventorymanager.com")
            .firstName("Jane")
            .lastName("User")
            .role(Role.USER)
            .enabled(true)
            .build();
        
        userRepository.save(admin);
        userRepository.save(manager);
        userRepository.save(user);
    }
    
    private void seedInventoryItems() {
        InventoryItem[] items = {
            createItem("Laptop Pro", "Electronics", 15, 1299.99, "TechCorp"),
            createItem("Wireless Mouse", "Electronics", 45, 29.99, "TechCorp"),
            createItem("Monitor 4K", "Electronics", 8, 399.99, "DisplayTech"),
            createItem("Office Chair", "Furniture", 25, 249.99, "ComfortSeats"),
            createItem("Desk Lamp", "Furniture", 30, 79.99, "LightUp"),
            createItem("Notebook Set", "Stationery", 5, 12.99, "PaperPlus"),
            createItem("Pen Pack", "Stationery", 60, 8.99, "WriteWell"),
            createItem("Coffee Maker", "Appliances", 12, 159.99, "BrewMaster"),
            createItem("Water Bottle", "Accessories", 3, 19.99, "HydroGear"),
            createItem("Keyboard", "Electronics", 20, 89.99, "TechCorp"),
            createItem("Wireless Headphones", "Electronics", 0, 199.99, "TechCorp"),
            createItem("Standing Desk", "Furniture", 7, 549.99, "ComfortSeats"),
            createItem("Printer Paper", "Stationery", 150, 24.99, "PaperPlus"),
            createItem("Desk Organizer", "Accessories", 35, 34.99, "WriteWell"),
            createItem("USB Cable", "Electronics", 2, 14.99, "TechCorp")
        };

        inventoryItemRepository.saveAll(Arrays.asList(items));
    }
    
    private InventoryItem createItem(String name, String category, int quantity, double price, String supplier) {
        return InventoryItem.builder()
            .name(name)
            .category(category)
            .quantity(quantity)
            .price(BigDecimal.valueOf(price))
            .supplier(supplier)
            .minStockLevel(quantity < 10 ? 5 : 10)
            .maxStockLevel(quantity < 10 ? 25 : 100)
            .createdBy("system")
            .updatedBy("system")
            .build();
    }
}