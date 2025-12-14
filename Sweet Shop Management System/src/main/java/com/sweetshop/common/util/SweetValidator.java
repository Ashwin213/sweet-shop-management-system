package com.sweetshop.common.util;

import com.sweetshop.sweet.dto.SweetRequest;

import java.math.BigDecimal;

public class SweetValidator {
    
    private SweetValidator() {
        // Utility class - prevent instantiation
    }
    
    public static void validate(SweetRequest request) {
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
    }
    
    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }
    
    private static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than or equal to 0");
        }
    }
}



