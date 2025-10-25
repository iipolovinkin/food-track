package com.foodtracker.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for conversion funnel analytics")
public class ConversionFunnelResponse {
    @Schema(description = "Category being analyzed", example = "pizza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;
    
    @Schema(description = "Number of items viewed", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private long viewedCount;
    
    @Schema(description = "Number of items added to cart", example = "75", requiredMode = Schema.RequiredMode.REQUIRED)
    private long addedCount;
    
    @Schema(description = "Number of items ordered", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private long orderedCount;
    
    @Schema(description = "Conversion rate as a percentage", example = "50.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private double conversionRate;
    
    @Schema(description = "Additional metrics for the conversion funnel")
    private Map<String, Object> additionalMetrics;
}