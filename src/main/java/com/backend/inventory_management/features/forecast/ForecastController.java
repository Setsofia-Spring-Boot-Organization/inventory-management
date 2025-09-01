// ForecastController.java
package com.backend.inventory_management.features.forecast;

import com.backend.inventory_management.core.Response;
import com.backend.inventory_management.features.forecast.dtos.ForecastDto;
import com.backend.inventory_management.features.forecast.dtos.ProductForecastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForecastController {
    
    private final ForecastService forecastService;
    
    @GetMapping("/overall")
    public ResponseEntity<Response<ForecastDto>> getOverallForecast(
            @RequestParam(defaultValue = "30") int days) {
        try {
            if (days < 1 || days > 365) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Days must be between 1 and 365"));
            }
            
            ForecastDto forecast = forecastService.getOverallForecast(days);
            return ResponseEntity.ok(Response.success(forecast));
        } catch (Exception e) {
            log.error("Failed to generate overall forecast", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to generate forecast"));
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<Response<ProductForecastDto>> getProductForecast(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            if (days < 1 || days > 365) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Days must be between 1 and 365"));
            }
            
            ProductForecastDto forecast = forecastService.getProductForecast(productId, days);
            return ResponseEntity.ok(Response.success(forecast));
        } catch (RuntimeException e) {
            log.error("Product not found for forecast: {}", productId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error("Product not found"));
        } catch (Exception e) {
            log.error("Failed to generate product forecast for id: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Failed to generate product forecast"));
        }
    }
}