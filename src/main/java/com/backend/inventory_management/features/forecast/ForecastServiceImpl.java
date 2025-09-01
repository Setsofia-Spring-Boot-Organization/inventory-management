package com.backend.inventory_management.features.forecast;

import com.backend.inventory_management.features.forecast.dtos.ForecastDto;
import com.backend.inventory_management.features.forecast.dtos.ForecastInsightDto;
import com.backend.inventory_management.features.forecast.dtos.ProductForecastDto;
import com.backend.inventory_management.features.inventory.InventoryItem;
import com.backend.inventory_management.features.inventory.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements ForecastService {
    
    private final InventoryItemRepository inventoryItemRepository;
    
    @Override
    public ForecastDto getOverallForecast(int days) {
        List<String> labels = generateLabels(days);
        List<Double> values = generateOverallForecastData(days);
        
        return ForecastDto.builder()
            .labels(labels)
            .values(values)
            .period(days + " days")
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(days))
            .build();
    }
    
    @Override
    public ProductForecastDto getProductForecast(Long productId, int days) {
        InventoryItem item = inventoryItemRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        List<String> labels = generateLabels(days);
        List<Double> values = generateProductForecastData(item, days);
        List<ForecastInsightDto> insights = generateProductInsights(item, values);
        
        ForecastDto forecast = ForecastDto.builder()
            .labels(labels)
            .values(values)
            .period(days + " days")
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(days))
            .build();
        
        return ProductForecastDto.builder()
            .productId(productId)
            .productName(item.getName())
            .forecast(forecast)
            .insights(insights)
            .build();
    }
    
    private List<String> generateLabels(int days) {
        return IntStream.rangeClosed(1, Math.min(days, 30))
            .mapToObj(i -> "Day " + i)
            .toList();
    }
    
    private List<Double> generateOverallForecastData(int days) {
        List<Double> data = new ArrayList<>();
        double baseValue = 50.0;
        
        for (int i = 1; i <= Math.min(days, 30); i++) {
            // Simulate seasonal trends and random variation
            double seasonal = Math.sin(i * 0.1) * 10;
            double random = (Math.random() - 0.5) * 20;
            double trend = i * 0.5; // Slight upward trend
            data.add(Math.max(0, baseValue + seasonal + random + trend));
        }
        
        return data;
    }
    
    private List<Double> generateProductForecastData(InventoryItem item, int days) {
        List<Double> data = new ArrayList<>();
        double baseValue = Math.max(1, item.getQuantity() / 10.0);
        double categoryMultiplier = getCategoryMultiplier(item.getCategory());
        
        for (int i = 1; i <= Math.min(days, 30); i++) {
            // Simulate product-specific demand patterns
            double seasonal = Math.sin(i * 0.15) * (baseValue * 0.3);
            double random = (Math.random() - 0.5) * (baseValue * 0.4);
            double trend = i * 0.1 * categoryMultiplier;
            
            data.add((double) Math.max(0, Math.round(baseValue + seasonal + random + trend)));
        }
        
        return data;
    }
    
    private double getCategoryMultiplier(String category) {
        Map<String, Double> multipliers = new HashMap<>();
        multipliers.put("Electronics", 1.2);
        multipliers.put("Clothing", 0.8);
        multipliers.put("Books", 0.6);
        multipliers.put("Home", 1.0);
        multipliers.put("Sports", 0.9);
        multipliers.put("Furniture", 0.7);
        multipliers.put("Stationery", 0.5);
        multipliers.put("Appliances", 1.1);
        multipliers.put("Accessories", 0.9);
        
        return multipliers.getOrDefault(category, 1.0);
    }
    
    private List<ForecastInsightDto> generateProductInsights(InventoryItem item, List<Double> forecastData) {
        List<ForecastInsightDto> insights = new ArrayList<>();
        
        double avgDemand = forecastData.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double maxDemand = forecastData.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double currentStock = item.getQuantity();
        double daysUntilStockout = avgDemand > 0 ? currentStock / avgDemand : 0;
        
        insights.add(ForecastInsightDto.builder()
            .label("Average Daily Demand")
            .value(String.format("%.1f units", avgDemand))
            .icon("📊")
            .description("Expected daily demand based on forecast")
            .build());
        
        insights.add(ForecastInsightDto.builder()
            .label("Peak Demand Expected")
            .value(String.format("%.0f units", maxDemand))
            .icon("📈")
            .description("Highest single day demand in forecast period")
            .build());
        
        insights.add(ForecastInsightDto.builder()
            .label("Current Stock Level")
            .value(String.format("%d units", item.getQuantity()))
            .icon("📦")
            .description("Current inventory quantity")
            .build());
        
        insights.add(ForecastInsightDto.builder()
            .label("Days Until Stockout")
            .value(String.format("%.1f days", daysUntilStockout))
            .icon("⏰")
            .description("Estimated days until inventory depletes")
            .build());
        
        return insights;
    }
}