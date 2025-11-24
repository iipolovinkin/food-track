package com.foodtracker.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopularItemDto {
    private String itemName;
    private Integer viewCount;
    private Integer cartAdditions;
    private Integer orders;
    private Double popularityScore;
}