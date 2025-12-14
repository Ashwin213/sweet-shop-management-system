package com.sweetshop.sweet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SearchRequest {
    private String name;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }
    
    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }
    
    public boolean hasPriceRange() {
        return minPrice != null && maxPrice != null;
    }
    
    public void validate() {
        if (hasPriceRange()) {
            if (minPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Minimum price must be greater than 0");
            }
            if (maxPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Maximum price must be greater than 0");
            }
            if (minPrice.compareTo(maxPrice) > 0) {
                throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
            }
        }
    }
}

