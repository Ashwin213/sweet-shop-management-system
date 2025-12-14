package com.sweetshop.common.util;

import com.sweetshop.exception.BadRequestException;

public class ValidationUtil {
    
    private ValidationUtil() {
        // Utility class - prevent instantiation
    }
    
    public static void validateQuantityGreaterThanZero(Integer quantity, String operation) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(
                String.format("%s quantity must be greater than zero", capitalize(operation))
            );
        }
    }
    
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}



