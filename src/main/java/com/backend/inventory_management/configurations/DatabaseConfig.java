package com.backend.inventory_management.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.backend.inventory_management")
@EnableTransactionManagement
public class DatabaseConfig { }
